package pl.devoxx.dxr.android.report;

import pl.devoxx.dxr.android.R;
import pl.devoxx.dxr.android.core.Callback;
import pl.devoxx.dxr.api.results.Result;

/**
 * Created by wilk on 09/08/15.
 */
public abstract class SponsorReportCallback implements Callback<Result>{

    @Override
    public void onSuccess(Result result) {
        if(result.isSuccessful()){
            sendToast(getString(R.string.sponsor_success));
        } else{
            sendToast(getString(R.string.sponsor_failure) + result.getMessage());
        }
    }

    @Override
    public void onError(Throwable serverError, Throwable localError) {
        sendToast(getString(R.string.sponsor_failure) + serverError.getMessage());
    }

    protected abstract void sendToast(String text);

    protected abstract String getString(int stringId);
}
