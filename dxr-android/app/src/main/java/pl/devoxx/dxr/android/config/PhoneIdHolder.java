package pl.devoxx.dxr.android.config;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wilk on 14/06/15.
 */
public class PhoneIdHolder {

    private static final AtomicInteger phoneId = new AtomicInteger(0);

    public static String getAsString(){
        return String.valueOf(phoneId.get());
    }

    public static void set(int newValue){
        phoneId.set(newValue);
    }

}
