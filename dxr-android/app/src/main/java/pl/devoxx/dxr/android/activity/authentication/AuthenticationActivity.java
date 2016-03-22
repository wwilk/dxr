package pl.devoxx.dxr.android.activity.authentication;

import android.content.Intent;
import android.widget.EditText;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import pl.devoxx.dxr.android.R;
import pl.devoxx.dxr.android.activity.DbActivity;
import pl.devoxx.dxr.android.activity.settings.SettingsActivity_;
import pl.devoxx.dxr.android.authentication.AuthenticationService;

/**
 * Created by wilk on 14/04/15.
 */
@EActivity(R.layout.authentication_activity)
public class AuthenticationActivity extends DbActivity {

    @ViewById
    protected EditText passwordText;

    @Click(R.id.loginBtn)
    public void onLoginBtnClick() {
        String password = passwordText.getText().toString();
        boolean correct = isPasswordCorrect(password);
        if(correct){
            goTo(SettingsActivity_.class);
        } else{
            sendToast("Not authorized to change settings.");
            finishFromChild(this);
        }
    }

    private boolean isPasswordCorrect(String givenPassword){
        String passwordHash = properties().getAuthenticationPasswordHash();
        AuthenticationService authenticationService = new AuthenticationService(passwordHash);
        return authenticationService.checkPassword(givenPassword);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        finish(); // go back to HomeActivity
    }

}
