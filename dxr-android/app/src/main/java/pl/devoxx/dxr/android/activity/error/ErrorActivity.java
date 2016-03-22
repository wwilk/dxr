package pl.devoxx.dxr.android.activity.error;

import android.os.AsyncTask;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import pl.devoxx.dxr.android.R;
import pl.devoxx.dxr.android.activity.DbActivity;
import pl.devoxx.dxr.api.error.AndroidErrorRemoteService;
import pl.devoxx.dxr.api.error.ErrorInfo;
import pl.devoxx.dxr.api.results.Result;

/**
 * Created by wilk on 15/03/15.
 */
@EActivity(R.layout.error_activity)
public class ErrorActivity extends DbActivity {

    @ViewById
    protected TextView error;

    @AfterViews
    protected void init(){
        ErrorInfo errorInfo = (ErrorInfo) getIntent().getSerializableExtra("error");
        logErrorOnServer(errorInfo);

        error.setMovementMethod(new ScrollingMovementMethod());
        error.setText(errorInfo.toString());
    }

    @Override
    protected void registerErrorHandler(){
        // no need to register error handler inside error handler
    }

    private void logErrorOnServer(final ErrorInfo info){
        final AndroidErrorRemoteService errorRemoteService = createServiceFactory().getErrorRemoteService();

        new AsyncTask<Void, Void, Result>() {

            @Override
            protected Result doInBackground(Void... errorInfos) {
                try {
                    return errorRemoteService.log(info);
                } catch(Exception e){
                    Log.e(getClass().getSimpleName(), "couldn't send error log to the server : " + e.getMessage());
                }
                return null;
            }
        }.execute();
    }
}
