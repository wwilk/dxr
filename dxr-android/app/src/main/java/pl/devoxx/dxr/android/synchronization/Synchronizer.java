package pl.devoxx.dxr.android.synchronization;

import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pl.devoxx.dxr.android.activity.VisibilityAwareActivity;
import pl.devoxx.dxr.android.synchronization.person.PersonSynchronizationService;
import pl.devoxx.dxr.api.appearance.AppearanceDto;
import pl.devoxx.dxr.api.appearance.AppearanceRemoteService;
import pl.devoxx.dxr.api.encryption.EncryptionService;
import pl.devoxx.dxr.api.nfc_relation.NfcRelationDto;
import pl.devoxx.dxr.api.nfc_relation.NfcRelationRemoteService;
import pl.devoxx.dxr.api.person.PersonDto;
import pl.devoxx.dxr.api.person.PersonRemoteService;

/**
 * Created by wilk on 22/03/15.
 */
public class Synchronizer {

    private final UpdateLocalService updateLocalService;
    private final SynchronizationService synchronizationService;
    private final UpdateRemoteService updateRemoteService;
    private final PersonRemoteService personRemoteService;
    private final AppearanceRemoteService appearanceRemoteService;
    private final NfcRelationRemoteService nfcRelationRemoteService;
    private final VisibilityAwareActivity activity;
    private final EncryptionService encryptionService;
    private final PersonSynchronizationService personSynchronizationService;

    public Synchronizer(UpdateLocalService updateLocalService
            , SynchronizationService synchronizationService, UpdateRemoteService updateRemoteService,
                        AppearanceRemoteService appearanceRemoteService, PersonRemoteService personRemoteService,
                        VisibilityAwareActivity activity, NfcRelationRemoteService nfcRelationRemoteService,
                        EncryptionService encryptionService, PersonSynchronizationService personSynchronizationService){
        this.updateLocalService = updateLocalService;
        this.synchronizationService = synchronizationService;
        this.updateRemoteService = updateRemoteService;
        this.personRemoteService = personRemoteService;
        this.appearanceRemoteService = appearanceRemoteService;
        this.activity = activity;
        this.nfcRelationRemoteService = nfcRelationRemoteService;
        this.encryptionService = encryptionService;
        this.personSynchronizationService = personSynchronizationService;
    }

    public void synchronizeAppearancesAndNfcAutomatically(){
        try {
            Log.d(getClass().getSimpleName(), "Synchronization started");
            long start = System.currentTimeMillis();
            Date lastSynchronizationDate = synchronizationService.getLastSynchronizationDate();
            Date newSynchronizationDate = new Date();

            int fetchedPersons = 0;

            Date lastPersonSynchronizationDate = personSynchronizationService.getLastSynchronizationDate();
            if(lastPersonSynchronizationDate == null){
                fetchedPersons = syncPersons(null);
            }

            int fetchedAppearances = fetchAppearancesFromServer(lastSynchronizationDate);
            int fetchedRelations = fetchRelationsFromServer(lastSynchronizationDate);

            int sentAppearances = updateRemoteService.updateAppearances();
            int sentRelations = updateRemoteService.updateRelations();

            long end = System.currentTimeMillis();

            try {
                String debugMessage = getDebugMessage(end - start, fetchedPersons, fetchedAppearances, fetchedRelations, sentAppearances, sentRelations, lastSynchronizationDate);
                debugLog(debugMessage);
                if(activity.isVisible()) {
                    Toast.makeText(activity, debugMessage, Toast.LENGTH_LONG).show();
                }
            }catch(Exception e){ // sending a toast has to be fail-safe here, so that synchronization doesn't fail because of it
                Log.e(getClass().getSimpleName(), "logging debug message failed" ,e);
            }

            synchronizationService.onSynchronizationFinished(newSynchronizationDate);
        } catch(Exception e){
            if(activity.isVisible()){
                Toast.makeText(activity, "synchronization failed " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            Log.e(Synchronizer.class.getSimpleName(), "synchronizing failed", e);
        }
    }

    public int synchronizePersonsManually(){
        return syncPersons(personSynchronizationService.getLastSynchronizationDate());
    }

    private int syncPersons(Date lastSynchronizationDate){
        Date newSyncDate = new Date();
        List<PersonDto> encryptedPersons = personRemoteService.getModified(lastSynchronizationDate);
        List<PersonDto> decryptedPersons = decryptPersons(encryptedPersons);
        updateLocalService.updatePersons(decryptedPersons);
        debugLog("Fetched from server : persons : " + decryptedPersons.size());
        personSynchronizationService.onSynchronizationFinished(newSyncDate, decryptedPersons.size());
        return decryptedPersons.size();
    }

    private List<PersonDto> decryptPersons(List<PersonDto> encryptedPersons){
        List<PersonDto> decryptedPersons = new ArrayList<PersonDto>(encryptedPersons.size());
        for(PersonDto encrypted : encryptedPersons){
            decryptedPersons.add(encryptionService.decrypt(encrypted));
        }
        return decryptedPersons;
    }

    private int fetchAppearancesFromServer(Date lastSynchronizationDate){
        List<AppearanceDto> changedAppearances = appearanceRemoteService.getModified(lastSynchronizationDate);
        updateLocalService.updateAppearances(changedAppearances);
        return changedAppearances.size();
    }

    private int fetchRelationsFromServer(Date lastSynchronizationDate){
        List<NfcRelationDto> changedRelations = nfcRelationRemoteService.getModified(lastSynchronizationDate);
        updateLocalService.updateRelations(changedRelations);
        return changedRelations.size();
    }

    private String getDebugMessage(long time, int fetchedPersons, int fetchedAppearances, int fetchedRelations,
                                   int sentAppearances, int sentRelations, Date lastSynch){
        StringBuilder message =  new StringBuilder(200).append("Synchronization took ").append(time).append(" ms. ");
        if(fetchedPersons == 0 && fetchedAppearances == 0 && fetchedRelations == 0){
            message.append("No records fetched.");
        } else{
            message.append("Fetched : ");
            if(fetchedPersons != 0){
                message.append(fetchedPersons).append(" persons, ");
            }
            if(fetchedAppearances != 0){
                message.append(fetchedAppearances).append(" appearances, ");
            }
            if(fetchedRelations != 0){
                message.append(fetchedRelations).append(" relations, ");
            }

            message.delete(message.length() - 2, message.length());
            message.append(". ");
        }

        if(sentAppearances == 0 && sentRelations == 0){
            message.append("No records sent.");
        } else{
            message.append("Sent : ");
            if(sentAppearances != 0){
                message.append(sentAppearances).append(" appearances, ");
            }
            if(sentRelations != 0){
                message.append(sentRelations).append(" relations, ");
            }
            message.delete(message.length() - 2, message.length());
            message.append(". ");
        }

        message.append("Previous synch : ").append(formatSynchDate(lastSynch));

        return message.toString();
    }

    private String formatSynchDate(Date lastSynch){
        return lastSynch == null ? "never" : new SimpleDateFormat("MM-dd HH:mm:ss", Locale.ENGLISH).format(lastSynch);
    }


    private void debugLog(String message){
        Log.d(getClass().getSimpleName(), message);
    }

}
