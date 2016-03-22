package pl.devoxx.dxr.android.registration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import pl.devoxx.dxr.android.BuildConfig;
import pl.devoxx.dxr.android.CustomTestRunner;
import pl.devoxx.dxr.android.TestUtils;
import pl.devoxx.dxr.android.registration.CheckRegistrationWithNfcTagTemplate.NfcTagRegistrationParams;
import pl.devoxx.dxr.android.rest.AsyncTaskResult;
import pl.devoxx.dxr.api.person.PersonDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created by wilk on 12/04/15.
 */
@RunWith(CustomTestRunner.class)
@Config(constants = BuildConfig.class)
public class CheckRegistrationWithNfcTagTemplateTest {
    private CheckRegistrationWithNfcTagTemplate template;
    private DefaultPersonRemoteService mockRemoteService;
    private PersonLocalService mockLocalService;

    private NfcTagRegistrationParams params;

    @Before
    public void setUp(){
        mockRemoteService = mock(DefaultPersonRemoteService.class);
        mockLocalService = mock(PersonLocalService.class);
        template = spy(new CheckRegistrationWithNfcTagTemplate(mockRemoteService, mockLocalService));
        params = new NfcTagRegistrationParams(TestUtils.randomString(), "RegistrationDay1");
    }

    @Test
    public void when_user_is_fetched_remotely_then_local_database_is_not_called(){
        PersonDto expected = new PersonDto();
        when(mockRemoteService.findByNfcTagId(params.getNfcTagId(), params.getAppearanceTypeName()))
                .thenReturn(expected);

        AsyncTaskResult<PersonDto> result = template.call(params);

        assertThat(result.getServerError()).isNull();
        assertThat(result.getLocalError()).isNull();
        assertThat(result.getResult()).isSameAs(expected);

        verify(template, never()).callLocalDatabase(params);
    }

    @Test
    public void when_remote_call_throws_exception_then_local_database_is_called(){
        Person expected = new Person();
        when(mockRemoteService.findByNfcTagId(params.getNfcTagId(), params.getAppearanceTypeName()))
                .thenThrow(new RuntimeException());
        when(mockLocalService.findByNfcTagId(params.getNfcTagId(), params.getAppearanceTypeName())).thenReturn(expected);

        AsyncTaskResult<PersonDto> result = template.call(params);

        assertThat(result.getServerError()).isNull();
        assertThat(result.getLocalError()).isNull();
        assertThat(result.getResult().getUserId()).isSameAs(expected.getUserId());

        verify(template).callLocalDatabase(params);
    }

    @Test
    public void when_both_remote_and_local_call_throw_exception_then_result_is_error(){
        RuntimeException expected = new RuntimeException();
        when(mockRemoteService.findByNfcTagId(params.getNfcTagId(), params.getAppearanceTypeName()))
                .thenThrow(expected);
        doThrow(expected).when(mockLocalService).findByNfcTagId(params.getNfcTagId(), params.getAppearanceTypeName());

        AsyncTaskResult<PersonDto> result = template.call(params);

        assertThat(result.getServerError()).isEqualTo(expected);
        assertThat(result.getLocalError()).isEqualTo(expected);
        assertThat(result.getResult()).isNull();
    }
}
