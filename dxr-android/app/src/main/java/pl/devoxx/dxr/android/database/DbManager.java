package pl.devoxx.dxr.android.database;

import android.content.Context;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wilk on 22/03/15.
 */
public class DbManager {

    private static final ConcurrentHashMap<String, DbHelper> databases = new ConcurrentHashMap<String, DbHelper>();

    public synchronized static DbHelper getInstance(Context context, String databaseName, int version){
        DbHelper helper = databases.get(databaseName);
        if(helper == null || helper.getVersion() != version){
            helper = new DbHelper(context, databaseName, version);
            databases.put(databaseName, helper);
        }
        return helper;
    }

    public synchronized static void release(String databaseName){
        databases.remove(databaseName);
    }
}
