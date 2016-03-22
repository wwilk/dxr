package pl.devoxx.dxr.android.core;

public interface Callback<T> {
	void onSuccess(T result);
    void onError(Throwable serverError, Throwable localError);
}
