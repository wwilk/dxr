package pl.devoxx.dxr.android.core;

import android.util.Log;

import pl.devoxx.dxr.android.rest.AsyncTaskResult;

/**
 * Created by wilk on 20/03/15.
 */
public abstract class Template<A, R> {

    protected abstract R callLocalDatabase(A argument);

    protected abstract R callServer(A argument);

    public AsyncTaskResult<R> call(A argument){
        try{
            return new AsyncTaskResult<R>(callServer(argument));
        } catch(Exception serverException){
            Log.e(this.getClass().getSimpleName(), "execute to server failed", serverException);
            try {
                return new AsyncTaskResult<R>(callLocalDatabase(argument));
            } catch(Exception daoException){
                Log.e(this.getClass().getSimpleName(), "execute to database failed", daoException);
                return new AsyncTaskResult<R>(serverException, daoException);
            }
        }
    }

}
