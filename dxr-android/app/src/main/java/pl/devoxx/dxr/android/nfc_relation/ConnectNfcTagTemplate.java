package pl.devoxx.dxr.android.nfc_relation;

import pl.devoxx.dxr.android.core.Template;
import pl.devoxx.dxr.api.nfc_relation.NfcRelationDto;
import pl.devoxx.dxr.api.nfc_relation.NfcRelationRemoteService;
import pl.devoxx.dxr.api.results.CreateRelationResult;

/**
 * Created by wojtekwilk on 06/02/16.
 */
public class ConnectNfcTagTemplate extends Template<NfcRelationDto, CreateRelationResult>{

    private final NfcRelationRemoteService nfcRelationRemoteService;
    private final NfcRelationLocalService nfcRelationLocalService;

    public ConnectNfcTagTemplate(NfcRelationRemoteService nfcRelationRemoteService, NfcRelationLocalService nfcRelationLocalService) {
        this.nfcRelationRemoteService = nfcRelationRemoteService;
        this.nfcRelationLocalService = nfcRelationLocalService;
    }

    @Override
    protected CreateRelationResult callLocalDatabase(NfcRelationDto argument) {
        if(argument.getNfcTagId() != null && argument.getUserId() != null){
            NfcRelation relation = new NfcRelation(argument);
            relation.setPersistedOnServer(false);
            return nfcRelationLocalService.save(relation);
        }
        return CreateRelationResult.SUCCESS;
    }

    @Override
    protected CreateRelationResult callServer(NfcRelationDto argument) {
        return nfcRelationRemoteService.create(argument);
    }
}
