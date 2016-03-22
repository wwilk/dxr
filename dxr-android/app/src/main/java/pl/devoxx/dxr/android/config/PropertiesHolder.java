package pl.devoxx.dxr.android.config;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by wilk on 02/05/15.
 */
public class PropertiesHolder {

    private static final String FILE_NAME = "application.properties";

    private static PropertiesHolder propertiesHolder;

    private Properties properties;

    private PropertiesHolder(){}

    public int getReadTimeout(){
        return Integer.parseInt(properties.getProperty("rest.read_timeout"));
    }

    public int getConnectTimeout(){
        return Integer.valueOf(properties.getProperty("rest.connect_timeout"));
    }

    public int getIncreasedReadTimeout(){
        return Integer.parseInt(properties.getProperty("rest.increased_read_timeout"));
    }

    public int getIncreasedConnectTimeout(){
        return Integer.valueOf(properties.getProperty("rest.increased_connect_timeout"));
    }

    public String getDbName(){
        return properties.getProperty("database.name");
    }

    public long getSynchronizationPeriod(){
        return Long.parseLong(properties.getProperty("synchronization.period"));
    }

    public int getDbVersion(){
        return Integer.parseInt(properties.getProperty("database.version"));
    }

    public String getAuthenticationPasswordHash(){
        return properties.getProperty("authentication.password");
    }

    public String getSecurityKey(){
        return properties.getProperty("synchronization.security.key");
    }

    public static synchronized PropertiesHolder instance(Context context){
        if(propertiesHolder == null){
            propertiesHolder = new PropertiesHolder();
            propertiesHolder.loadProperties(context);
        }
        return propertiesHolder;
    }

    private void loadProperties(Context context){
        AssetManager assetManager = context.getResources().getAssets();
        properties = new Properties();
        InputStreamReader isr = null;
        try {
            isr = new InputStreamReader(assetManager.open(FILE_NAME), "UTF-8");
            properties.load(isr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally{
            if(isr != null)
                try {
                    isr.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
        }
    }
}
