package pl.devoxx.dxr.android.appearance;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import pl.devoxx.dxr.android.BuildConfig;
import pl.devoxx.dxr.android.CustomTestRunner;
import pl.devoxx.dxr.android.R;
import pl.devoxx.dxr.android.activity.user_info.UserInfoScreenAdapter;
import pl.devoxx.dxr.api.results.Result;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by wilk on 12/04/15.
 */
@RunWith(CustomTestRunner.class)
@Config(constants = BuildConfig.class)
public class InsertAppearanceCallbackTest {
    private InsertAppearanceCallback callback;
    private UserInfoScreenAdapter mockAdapter;

    @Before
    public void setUp() {
        mockAdapter = mock(UserInfoScreenAdapter.class);
        callback = new InsertAppearanceCallback(mockAdapter);
    }

    @Test
    public void when_appearance_is_successfully_saved_then_correct_toast_is_sent(){
        callback.onSuccess(new Result());

        verify(mockAdapter).getMessage(R.string.appearance_save_success);
        verify(mockAdapter).sendToast(anyString());
    }

    @Test
    public void when_error_occurs_then_correct_toast_is_sent(){
        callback.onError(new RuntimeException(), new RuntimeException());

        verify(mockAdapter).getMessage(R.string.appearance_save_error);
        verify(mockAdapter).sendToast(anyString());
    }
}
