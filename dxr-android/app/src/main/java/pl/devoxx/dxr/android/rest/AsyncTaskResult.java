package pl.devoxx.dxr.android.rest;

/**
 * Created by wilk on 12/03/15.
 */
public class AsyncTaskResult<T> {

    private final T result;
    private final Throwable serverError;
    private final Throwable localError;

    public AsyncTaskResult(T result){
        this.result = result;
        serverError = null;
        localError = null;
    }

    public AsyncTaskResult(Throwable serverError, Throwable localError){
        this.result = null;
        this.serverError = serverError;
        this.localError = localError;
    }

    public Throwable getServerError(){
        return serverError;
    }

    public Throwable getLocalError(){
        return localError;
    }

    public T getResult(){
        return result;
    }
}
