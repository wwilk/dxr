package pl.devoxx.dxr.android.synchronization;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import pl.devoxx.dxr.android.BuildConfig;
import pl.devoxx.dxr.android.CustomTestRunner;
import pl.devoxx.dxr.android.TestUtils;
import pl.devoxx.dxr.android.appearance.Appearance;
import pl.devoxx.dxr.android.appearance.AppearanceLocalService;
import pl.devoxx.dxr.android.appearance.DefaultAppearanceRemoteService;
import pl.devoxx.dxr.android.nfc_relation.DefaultNfcRelationRemoteService;
import pl.devoxx.dxr.android.nfc_relation.NfcRelation;
import pl.devoxx.dxr.android.nfc_relation.NfcRelationLocalService;
import pl.devoxx.dxr.api.appearance.AppearanceDto;
import pl.devoxx.dxr.api.nfc_relation.NfcRelationDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by wilk on 11/04/15.
 */
@RunWith(CustomTestRunner.class)
@Config(constants = BuildConfig.class)
public class UpdateRemoteServiceTest {

    private UpdateRemoteService updateService;
    private DefaultAppearanceRemoteService mockAppearanceRemoteService;
    private AppearanceLocalService mockAppearanceLocalService;
    private NfcRelationLocalService mockRelationLocalService;
    private DefaultNfcRelationRemoteService mockRelationRemoteService;

    @Before
    public void setUp(){
        mockAppearanceRemoteService = mock(DefaultAppearanceRemoteService.class);
        mockAppearanceLocalService = mock(AppearanceLocalService.class);
        mockRelationLocalService = mock(NfcRelationLocalService.class);
        mockRelationRemoteService = mock(DefaultNfcRelationRemoteService.class);
        updateService = new UpdateRemoteService(mockAppearanceRemoteService, mockAppearanceLocalService, mockRelationRemoteService, mockRelationLocalService);
    }

    @Test
    public void all_not_persisted_on_server_appearances_are_persisted_and_flag_is_set_and_entity_is_updated(){
        List<Appearance> notPersistedAppearances = Arrays.asList(notPersistedAppearance(), notPersistedAppearance());
        when(mockAppearanceLocalService.getAllAppearancesNotPersistedOnServer()).thenReturn(notPersistedAppearances);

        for(final Appearance appearance : notPersistedAppearances){
            assertThat(appearance.isPersistedOnServer()).isFalse();
        }

        int result = updateService.updateAppearances();

        for(final Appearance appearance : notPersistedAppearances){
            assertThat(appearance.isPersistedOnServer()).isTrue();
        }
        assertThat(result).isEqualTo(2);
        verify(mockAppearanceRemoteService, times(2)).create(any(AppearanceDto.class));
    }

    @Test
    public void all_not_persisted_on_server_relations_are_persisted_and_flag_is_set_and_entity_is_updated(){
        List<NfcRelation> notPersistedRelations = Arrays.asList(notPersistedRelation(), notPersistedRelation());
        when(mockRelationLocalService.getAllRelationsNotPersistedOnServer()).thenReturn(notPersistedRelations);

        for(final NfcRelation relation : notPersistedRelations){
            assertThat(relation.isPersistedOnServer()).isFalse();
        }

        int result = updateService.updateRelations();

        for(final NfcRelation relation : notPersistedRelations){
            assertThat(relation.isPersistedOnServer()).isTrue();
        }
        assertThat(result).isEqualTo(2);
        verify(mockRelationRemoteService, times(2)).create(any(NfcRelationDto.class));
    }

    private Appearance notPersistedAppearance(){
        Appearance appearance = new Appearance();
        appearance.setPersistedOnServer(false);
        appearance.setDate(new Date());
        return appearance;
    }

    private NfcRelation notPersistedRelation(){
        NfcRelation relation = new NfcRelation();
        relation.setPersistedOnServer(false);
        relation.setUserId(TestUtils.randomString(10));
        relation.setNfcTagId(TestUtils.randomString(10));
        return relation;
    }
}
