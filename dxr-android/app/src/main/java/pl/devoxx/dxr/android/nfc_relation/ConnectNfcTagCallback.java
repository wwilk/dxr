package pl.devoxx.dxr.android.nfc_relation;

import pl.devoxx.dxr.android.core.Callback;
import pl.devoxx.dxr.api.nfc_relation.NfcRelationDto;
import pl.devoxx.dxr.api.results.CreateRelationResult;

/**
 * Created by wojtekwilk on 06/02/16.
 */
public abstract class ConnectNfcTagCallback implements Callback<CreateRelationResult>{

    private final NfcRelationLocalService nfcRelationLocalService;
    private final NfcRelationDto nfcRelationDto;

    public ConnectNfcTagCallback(NfcRelationLocalService nfcRelationLocalService, NfcRelationDto nfcRelationDto){
        this.nfcRelationLocalService = nfcRelationLocalService;
        this.nfcRelationDto = nfcRelationDto;
    }

    protected abstract void onSuccess();
    protected abstract void onErrorResult(CreateRelationResult result);
    protected abstract void onExceptionThrown(Throwable serverError);

    @Override
    public void onSuccess(CreateRelationResult result) {
        if(result == CreateRelationResult.SUCCESS) {
            NfcRelation relation = new NfcRelation(nfcRelationDto);
            relation.setPersistedOnServer(true);
            nfcRelationLocalService.save(relation);
            onSuccess();
        } else {
            onErrorResult(result);
        }
    }

    @Override
    public void onError(Throwable serverError, Throwable localError) {
        onExceptionThrown(serverError);
    }
}
