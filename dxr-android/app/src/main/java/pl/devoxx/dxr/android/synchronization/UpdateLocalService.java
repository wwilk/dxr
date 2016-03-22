package pl.devoxx.dxr.android.synchronization;

import android.util.Log;

import java.util.List;

import pl.devoxx.dxr.android.appearance.Appearance;
import pl.devoxx.dxr.android.appearance.AppearanceLocalService;
import pl.devoxx.dxr.android.nfc_relation.NfcRelation;
import pl.devoxx.dxr.android.nfc_relation.NfcRelationLocalService;
import pl.devoxx.dxr.android.registration.Person;
import pl.devoxx.dxr.android.registration.PersonLocalService;
import pl.devoxx.dxr.api.appearance.AppearanceDto;
import pl.devoxx.dxr.api.nfc_relation.NfcRelationDto;
import pl.devoxx.dxr.api.person.PersonDto;
import pl.devoxx.dxr.api.results.CreateRelationResult;

/**
 * Created by wilk on 22/03/15.
 */
public class UpdateLocalService {

    private final AppearanceLocalService appearanceLocalService;
    private final PersonLocalService personLocalService;
    private final NfcRelationLocalService nfcRelationLocalService;

    public UpdateLocalService(AppearanceLocalService appearanceLocalService, PersonLocalService personLocalService,
                              NfcRelationLocalService nfcRelationLocalService){
        this.appearanceLocalService = appearanceLocalService;
        this.personLocalService = personLocalService;
        this.nfcRelationLocalService = nfcRelationLocalService;
    }

    public void updateAppearances(List<AppearanceDto> appearances){
        for(AppearanceDto dto : appearances){
            boolean exists = appearanceLocalService.existsAppearance(
                    dto.getUserId(), dto.getDate(), dto.getAppearanceTypeName());
            if(!exists){
                Appearance appearance = new Appearance(dto);
                appearance.setPersistedOnServer(true);
                appearanceLocalService.create(appearance);
            }
        }
    }

    public void updateRelations(List<NfcRelationDto> relations){
        for(NfcRelationDto dto : relations){
            NfcRelation relation = new NfcRelation(dto);
            relation.setPersistedOnServer(true);
            CreateRelationResult result = nfcRelationLocalService.save(relation);
            if(result != CreateRelationResult.SUCCESS){
                Log.i(getClass().getSimpleName(), "nfc relation not persisted locally due to " + result);
            }
        }
    }

    public void updatePersons(List<PersonDto> persons){
        for(PersonDto dto : persons){
            Person alreadyPersisted = personLocalService.findByUserId(dto.getUserId());
            if(alreadyPersisted == null) {
                personLocalService.create(new Person(dto));
            } else{
                Person updatedPerson = new Person(dto);
                updatedPerson.setId(alreadyPersisted.getId());
                updatedPerson.setServerUrl(alreadyPersisted.getServerUrl());
                personLocalService.update(updatedPerson);
            }
        }
    }
}