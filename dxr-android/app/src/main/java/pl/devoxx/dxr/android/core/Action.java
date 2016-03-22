package pl.devoxx.dxr.android.core;

/**
 * Created by wilk on 20/03/15.
 */
public interface Action<A, R> {
    R execute(A argument);
}
