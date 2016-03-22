package pl.devoxx.dxr.android.rest;

import android.os.AsyncTask;

import pl.devoxx.dxr.android.core.Callback;
import pl.devoxx.dxr.android.core.Template;

/**
 * Created by wilk on 20/03/15.
 */
public class RestAsyncTask<A,R> extends AsyncTask<Void, Void, AsyncTaskResult<R>> {

    private final Template<A,R> template;
    private final A argument;
    private final Callback<R> callback;

    public RestAsyncTask(Template<A,R> template, A argument, Callback<R> callback){
        this.template = template;
        this.argument = argument;
        this.callback = callback;
    }

    @Override
    protected AsyncTaskResult<R> doInBackground(Void... params) {
        return template.call(argument);
    }

    @Override
    protected void onPostExecute(AsyncTaskResult<R> result) {
        if(result.getServerError() != null){
            callback.onError(result.getServerError(), result.getLocalError());
        } else{
            callback.onSuccess(result.getResult());
        }
    }
}
