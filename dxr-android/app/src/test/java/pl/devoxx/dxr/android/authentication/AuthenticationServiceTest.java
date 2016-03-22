package pl.devoxx.dxr.android.authentication;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import pl.devoxx.dxr.android.BuildConfig;
import pl.devoxx.dxr.android.CustomTestRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by wilk on 14/04/15.
 */
@RunWith(CustomTestRunner.class)
@Config(constants = BuildConfig.class)
public class AuthenticationServiceTest {

    private AuthenticationService authenticationService;
    // sha1 hash of "test"
    private String password = "ee26b0dd4af7e749aa1a8ee3c10ae9923f618980772e473f8819a5d4940e0db27ac185f8a0e1d5f84f88bc887fd67b143732c304cc5fa9ad8e6f57f50028a8ff";

    @Before
    public void setUp(){
        authenticationService = new AuthenticationService(password);
    }

    @Test
    public void when_password_correct_return_true(){
        assertThat(authenticationService.checkPassword("test")).isTrue();
    }

    @Test
    public void when_password_is_not_correct_return_false(){
        assertThat(authenticationService.checkPassword("incorrectPassword")).isFalse();
    }

    @Test
    public void when_password_is_null_return_false(){
        assertThat(authenticationService.checkPassword(null)).isFalse();
    }
}
