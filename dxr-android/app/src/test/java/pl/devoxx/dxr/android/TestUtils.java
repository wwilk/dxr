package pl.devoxx.dxr.android;

import java.util.UUID;

/**
 * Created by wilk on 12/04/15.
 */
public class TestUtils {

    public static String randomString(int size){
        return UUID.randomUUID().toString().substring(0, size);
    }

    public static String randomString(){
        return UUID.randomUUID().toString().substring(0, 16);
    }
}
