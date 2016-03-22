package pl.devoxx.dxr.android.authentication;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import java.nio.charset.Charset;

/**
 * Created by wilk on 14/04/15.
 */
public class AuthenticationService {

    private final String hashedPassword;

    public AuthenticationService(String hashedPassword){
        this.hashedPassword = hashedPassword;
    }

    public boolean checkPassword(String userInput){
        if(userInput == null){
            return false;
        }
        try {
            HashCode hash = Hashing.sha512().hashString(userInput, Charset.forName("UTF-8"));
            return this.hashedPassword.equals(hash.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
