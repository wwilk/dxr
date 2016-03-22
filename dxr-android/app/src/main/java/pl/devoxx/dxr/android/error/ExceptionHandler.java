package pl.devoxx.dxr.android.error;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

import pl.devoxx.dxr.android.activity.error.ErrorActivity_;
import pl.devoxx.dxr.api.error.ErrorInfo;
import pl.devoxx.dxr.api.error.ErrorInfoBuilder;

/**
 * Created by wilk on 15/03/15.
 * based on
 * @see <a href="https://trivedihardik.wordpress.com/2011/08/20/how-to-avoid-force-close-error-in-android/">https://trivedihardik.wordpress.com/2011/08/20/how-to-avoid-force-close-error-in-android/</a>
 */
public class ExceptionHandler implements Thread.UncaughtExceptionHandler {
    private final Activity context;

    public ExceptionHandler(Activity context) {
        this.context = context;
    }

    public void uncaughtException(Thread thread, Throwable exception) {
        Log.e(ExceptionHandler.class.getSimpleName(), "UncaughtException", exception);
        StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));

        ErrorInfo error = new ErrorInfoBuilder()
                .stackTrace(stackTrace.toString()).brand(Build.BRAND)
                .device(Build.DEVICE).model(Build.MODEL).id(Build.ID)
                .product(Build.PRODUCT).sdk(Build.VERSION.SDK_INT)
                .release(Build.VERSION.RELEASE).incremental(Build.VERSION.INCREMENTAL).createError();

        Intent intent = new Intent(context, ErrorActivity_.class);

        intent.putExtra("error", error);
        context.startActivity(intent);

        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }

}
