package pl.devoxx.dxr.android.synchronization;

import android.os.AsyncTask;

import pl.devoxx.dxr.android.rest.AsyncTaskResult;

/**
 * Created by wilk on 20/12/15.
 */
public abstract class ManualSynchronizationAsyncTask extends AsyncTask<Void, Void, AsyncTaskResult<Integer>>{

    private final Synchronizer synchronizer;

    public ManualSynchronizationAsyncTask(Synchronizer synchronizer) {
        this.synchronizer = synchronizer;
    }

    @Override
    protected AsyncTaskResult<Integer> doInBackground(Void... voids) {
        try{
            return new AsyncTaskResult<Integer>(synchronizer.synchronizePersonsManually());
        } catch(Exception e){
            return new AsyncTaskResult<Integer>(e, null);
        }
    }

    @Override
    protected void onPostExecute(AsyncTaskResult<Integer> result) {
        if(result.getServerError() != null){
            sendToast("Person synchronization failed " + result.getServerError().getMessage());
        } else{
            sendToast("Person synchronization succeeded. " + result.getResult() + " persons updated.");
        }
    }

    protected abstract void sendToast(String message);
}
