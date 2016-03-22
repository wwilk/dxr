package pl.devoxx.dxr.android.registration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Collections;

import pl.devoxx.dxr.android.BuildConfig;
import pl.devoxx.dxr.android.CustomTestRunner;
import pl.devoxx.dxr.android.R;
import pl.devoxx.dxr.android.activity.user_info.UserInfoScreenAdapter;
import pl.devoxx.dxr.android.appearance.InsertAppearanceTaskFactory;
import pl.devoxx.dxr.android.rest.RestAsyncTask;
import pl.devoxx.dxr.api.appearance.AppearanceDto;
import pl.devoxx.dxr.api.person.PersonDto;
import pl.devoxx.dxr.api.person.Status;
import pl.devoxx.dxr.api.results.Result;

import static org.mockito.Mockito.*;

/**
 * Created by wilk on 12/04/15.
 */
@RunWith(CustomTestRunner.class)
@Config(constants = BuildConfig.class)
public class CheckRegistrationCallbackTest {
    private CheckRegistrationCallback callback;
    private UserInfoScreenAdapter mockUserInfoScreenAdapter;
    private InsertAppearanceTaskFactory mockAppearanceTaskFactory;
    private RestAsyncTask<AppearanceDto, Result> mockAsyncTask;

    @Before
    public void setUp() {
        mockUserInfoScreenAdapter = mock(UserInfoScreenAdapter.class);
        mockAppearanceTaskFactory = mock(InsertAppearanceTaskFactory.class);
        mockAsyncTask = mock(RestAsyncTask.class);
        when(mockAppearanceTaskFactory.create(any(String.class))).thenReturn(mockAsyncTask);
        callback = new CheckRegistrationCallback(mockUserInfoScreenAdapter, mockAppearanceTaskFactory);
    }

    @Test
    public void person_is_null(){
        callback.onSuccess(null);

        verify(mockUserInfoScreenAdapter).getMessage(R.string.registration_user_not_found);
        verify(mockUserInfoScreenAdapter).fillRed();
        verify(mockUserInfoScreenAdapter).playFailureSound();
        verify(mockUserInfoScreenAdapter).sendToast(anyString());
        verify(mockUserInfoScreenAdapter).hideWriteButton();
    }

    @Test
    public void person_paid_but_already_appeared_before(){
        PersonDto personDto = new PersonDto();
        personDto.setAppearances(Arrays.asList(new AppearanceDto()));
        personDto.setRegistrationStatus(Status.Paid);
        callback.onSuccess(personDto);

        verify(mockUserInfoScreenAdapter).getMessage(R.string.registration_user_already_appeared);
        verify(mockUserInfoScreenAdapter).fillRed();
        verify(mockUserInfoScreenAdapter).playFailureSound();
        verify(mockUserInfoScreenAdapter).sendToast(anyString());
        verify(mockAsyncTask).execute();
    }

    @Test
    public void person_paid_and_didnt_appear_before(){
        PersonDto personDto = new PersonDto();
        personDto.setAppearances(Collections.EMPTY_LIST);
        personDto.setRegistrationStatus(Status.Paid);
        callback.onSuccess(personDto);

        verify(mockUserInfoScreenAdapter).playSuccessSound();
        verify(mockUserInfoScreenAdapter).getMessage(R.string.registration_user_paid_and_first_appearance);
        verify(mockUserInfoScreenAdapter).fillGreen();
        verify(mockUserInfoScreenAdapter).sendToast(anyString());
        verify(mockAsyncTask).execute();
    }

    @Test
    public void person_didnt_pay(){
        PersonDto personDto = new PersonDto();
        personDto.setAppearances(Collections.EMPTY_LIST);
        personDto.setRegistrationStatus(Status.Canceled);
        callback.onSuccess(personDto);

        verify(mockUserInfoScreenAdapter).playFailureSound();
        verify(mockUserInfoScreenAdapter).getMessage(R.string.registration_user_didnt_pay);
        verify(mockUserInfoScreenAdapter).fillYellow();
        verify(mockUserInfoScreenAdapter).sendToast(anyString());
        verify(mockAsyncTask).execute();
    }

    @Test
    public void when_error_occurs_then_correct_toast_is_sent(){
        callback.onError(new RuntimeException(), new RuntimeException());

        verify(mockUserInfoScreenAdapter).playFailureSound();
        verify(mockUserInfoScreenAdapter, times(2)).sendToast(anyString());
        verify(mockUserInfoScreenAdapter).fillRed();
    }
}
