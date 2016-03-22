package pl.devoxx.dxr.android.synchronization;

import android.util.Log;

import java.util.List;

import pl.devoxx.dxr.android.appearance.Appearance;
import pl.devoxx.dxr.android.appearance.AppearanceLocalService;
import pl.devoxx.dxr.android.nfc_relation.NfcRelation;
import pl.devoxx.dxr.android.nfc_relation.NfcRelationLocalService;
import pl.devoxx.dxr.api.appearance.AppearanceRemoteService;
import pl.devoxx.dxr.api.nfc_relation.NfcRelationRemoteService;

/**
 * Created by wilk on 23/03/15.
 */
public class UpdateRemoteService {

    private final AppearanceRemoteService appearanceRemoteService;
    private final AppearanceLocalService appearanceLocalService;
    private final NfcRelationRemoteService nfcRelationRemoteService;
    private final NfcRelationLocalService nfcRelationLocalService;

    public UpdateRemoteService(AppearanceRemoteService appearanceRemoteService, AppearanceLocalService appearanceLocalService,
                               NfcRelationRemoteService nfcRelationRemoteService, NfcRelationLocalService nfcRelationLocalService){
        this.appearanceRemoteService = appearanceRemoteService;
        this.appearanceLocalService = appearanceLocalService;
        this.nfcRelationRemoteService = nfcRelationRemoteService;
        this.nfcRelationLocalService = nfcRelationLocalService;
    }

    public int updateAppearances(){
        try {
            List<Appearance> notPersistedOnServer = appearanceLocalService.getAllAppearancesNotPersistedOnServer();
            for (Appearance appearance : notPersistedOnServer) {
                appearanceRemoteService.create(appearance.toDto());
                appearance.setPersistedOnServer(true);
                appearanceLocalService.update(appearance);
            }
            return notPersistedOnServer.size();
        }catch(Exception e){
            Log.e(this.getClass().getSimpleName(), "remote update failed", e);
            return 0;
        }
    }

    public int updateRelations(){
        try {
            List<NfcRelation> notPersistedOnServer = nfcRelationLocalService.getAllRelationsNotPersistedOnServer();
            for (NfcRelation relation : notPersistedOnServer) {
                nfcRelationRemoteService.create(relation.toDto());
                relation.setPersistedOnServer(true);
                nfcRelationLocalService.update(relation);
            }
            return notPersistedOnServer.size();
        }catch(Exception e){
            Log.e(this.getClass().getSimpleName(), "remote update failed", e);
            return 0;
        }
    }
}
