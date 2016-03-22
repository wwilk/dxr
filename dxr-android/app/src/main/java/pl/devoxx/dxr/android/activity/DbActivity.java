package pl.devoxx.dxr.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import pl.devoxx.dxr.android.config.PropertiesHolder;
import pl.devoxx.dxr.android.core.ServiceFactory;
import pl.devoxx.dxr.android.database.DbHelper;
import pl.devoxx.dxr.android.database.DbManager;
import pl.devoxx.dxr.android.error.ExceptionHandler;

/**
 * Created by wilk on 03/05/15.
 */
public class DbActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerErrorHandler();
    }

    protected void registerErrorHandler(){
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DbManager.release(properties().getDbName());
    }

    protected DbHelper getHelper() {
        return DbManager.getInstance(getApplicationContext(), properties().getDbName(),
                properties().getDbVersion());
    }

    protected void sendToast(String text) {
        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
        toast.show();
    }

    protected ServiceFactory createServiceFactory(){
        return ServiceFactory.withShortTimeoutAndDynamicServerUrl(properties(), getHelper());
    }

    protected PropertiesHolder properties() {
        return PropertiesHolder.instance(this);
    }

    protected void goTo(Class<? extends Activity> screen){
        Intent intent = new Intent(this, screen);
        startActivity(intent);
    }
}
