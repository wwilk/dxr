package pl.devoxx.dxr.android.appearance;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.annotation.Config;

import pl.devoxx.dxr.android.BuildConfig;
import pl.devoxx.dxr.android.CustomTestRunner;
import pl.devoxx.dxr.android.rest.AsyncTaskResult;
import pl.devoxx.dxr.api.appearance.AppearanceDto;
import pl.devoxx.dxr.api.results.Result;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by wilk on 12/04/15.
 */
@RunWith(CustomTestRunner.class)
@Config(constants = BuildConfig.class)
public class InsertAppearanceTemplateTest {

    private DefaultAppearanceRemoteService appearanceRemoteService;
    private AppearanceLocalService appearanceLocalService;
    private InsertAppearanceTemplate template;

    @Before
    public void setUp(){
        appearanceRemoteService = mock(DefaultAppearanceRemoteService.class);
        appearanceLocalService = mock(AppearanceLocalService.class);
        template = spy(new InsertAppearanceTemplate(appearanceRemoteService, appearanceLocalService));
    }

    @Test
    public void when_appearance_is_saved_remotely_then_local_database_is_not_called(){
        Result expected = new Result(true, "success");
        when(appearanceRemoteService.create(any(AppearanceDto.class)))
                .thenReturn(expected);

        AsyncTaskResult<Result> result = template.call(new AppearanceDto());

        assertThat(result.getServerError()).isNull();
        assertThat(result.getLocalError()).isNull();
        assertThat(result.getResult()).isSameAs(expected);

        ArgumentCaptor<Appearance> argument = ArgumentCaptor.forClass(Appearance.class);
        verify(appearanceLocalService).create(argument.capture());
        assertThat(argument.getValue().isPersistedOnServer()).isTrue();

        verify(template, never()).callLocalDatabase(any(AppearanceDto.class));
    }

    @Test
    public void when_remote_call_throws_exception_then_local_database_is_called(){
        when(appearanceRemoteService.create(any(AppearanceDto.class)))
                .thenThrow(new RuntimeException());

        AsyncTaskResult<Result> result = template.call(new AppearanceDto());

        assertThat(result.getServerError()).isNull();
        assertThat(result.getLocalError()).isNull();
        assertThat(result.getResult().isSuccessful()).isTrue();

        ArgumentCaptor<Appearance> argument = ArgumentCaptor.forClass(Appearance.class);
        verify(appearanceLocalService).create(argument.capture());
        assertThat(argument.getValue().isPersistedOnServer()).isFalse();

        verify(template).callLocalDatabase(any(AppearanceDto.class));
    }

    @Test
    public void when_both_remote_and_local_call_throw_exception_then_result_is_error(){
        RuntimeException expected = new RuntimeException();
        when(appearanceRemoteService.create(any(AppearanceDto.class)))
                .thenThrow(expected);
        doThrow(expected).when(appearanceLocalService).create(any(Appearance.class));

        AsyncTaskResult<Result> result = template.call(new AppearanceDto());

        assertThat(result.getServerError()).isEqualTo(expected);
        assertThat(result.getLocalError()).isEqualTo(expected);
        assertThat(result.getResult()).isNull();
    }
}
