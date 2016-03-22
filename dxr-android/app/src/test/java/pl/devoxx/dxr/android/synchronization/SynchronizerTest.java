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
import pl.devoxx.dxr.android.activity.VisibilityAwareActivity;
import pl.devoxx.dxr.android.appearance.DefaultAppearanceRemoteService;
import pl.devoxx.dxr.android.nfc_relation.DefaultNfcRelationRemoteService;
import pl.devoxx.dxr.android.registration.DefaultPersonRemoteService;
import pl.devoxx.dxr.android.synchronization.person.PersonSynchronizationService;
import pl.devoxx.dxr.api.appearance.AppearanceDto;
import pl.devoxx.dxr.api.encryption.EncryptionService;
import pl.devoxx.dxr.api.nfc_relation.NfcRelationDto;
import pl.devoxx.dxr.api.person.PersonDto;

import static org.mockito.Mockito.*;

/**
 * Created by wilk on 12/04/15.
 */
@RunWith(CustomTestRunner.class)
@Config(constants = BuildConfig.class)
public class SynchronizerTest {

    private Synchronizer synchronizer;
    private UpdateLocalService mockUpdateLocalService;
    private SynchronizationService mockSynchronizationService;
    private UpdateRemoteService mockUpdateRemoteService;
    private DefaultPersonRemoteService mockPersonRemoteService;
    private DefaultAppearanceRemoteService mockAppearanceRemoteService;
    private DefaultNfcRelationRemoteService mockRelationRemoteService;
    private VisibilityAwareActivity mockActivity;
    private EncryptionService mockEncryptionService;
    private PersonSynchronizationService mockPersonSynchronizationService;

    @Before
    public void setUp(){
        mockUpdateLocalService = mock(UpdateLocalService.class);
        mockSynchronizationService = mock(SynchronizationService.class);
        mockUpdateRemoteService = mock(UpdateRemoteService.class);
        mockPersonRemoteService = mock(DefaultPersonRemoteService.class);
        mockAppearanceRemoteService = mock(DefaultAppearanceRemoteService.class);
        mockRelationRemoteService = mock(DefaultNfcRelationRemoteService.class);
        mockActivity = mock(VisibilityAwareActivity.class);
        mockEncryptionService = mock(EncryptionService.class);
        mockPersonSynchronizationService = mock(PersonSynchronizationService.class);
        synchronizer = new Synchronizer(mockUpdateLocalService, mockSynchronizationService, mockUpdateRemoteService,
                mockAppearanceRemoteService, mockPersonRemoteService, mockActivity, mockRelationRemoteService, mockEncryptionService,
                mockPersonSynchronizationService);
    }

    @Test
    public void when_synch_date_is_not_null_then_appearances_and_relations_returned_by_remote_services_are_passed_to_update_local_service(){
        Date lastSynchronizationDate = new Date();
        List<AppearanceDto> appearances = Arrays.asList(appearance(), appearance());
        List<PersonDto> persons = Arrays.asList(new PersonDto(), new PersonDto(), new PersonDto());
        List<NfcRelationDto> relations = Arrays.asList(relation(), relation(), relation());
        when(mockSynchronizationService.getLastSynchronizationDate()).thenReturn(lastSynchronizationDate);
        when(mockPersonRemoteService.getModified(lastSynchronizationDate)).thenReturn(persons);
        when(mockAppearanceRemoteService.getModified(lastSynchronizationDate)).thenReturn(appearances);
        when(mockRelationRemoteService.getModified(lastSynchronizationDate)).thenReturn(relations);
        synchronizer.synchronizeAppearancesAndNfcAutomatically();

        verify(mockUpdateLocalService).updateAppearances(appearances);
        verify(mockUpdateLocalService).updateRelations(relations);
        verify(mockUpdateRemoteService).updateAppearances();
        verify(mockUpdateRemoteService).updateRelations();
        verify(mockSynchronizationService).onSynchronizationFinished(any(Date.class));
    }

    @Test
    public void when_synch_date_is_not_null_then_appearances_and_PERSONS_and_relations_returned_by_remote_services_are_passed_to_update_local_service(){
        Date lastSynchronizationDate = null;
        List<AppearanceDto> appearances = Arrays.asList(appearance(), appearance());
        PersonDto dto1 = new PersonDto();
        PersonDto dto1Decrypted = new PersonDto();
        PersonDto dto2 = new PersonDto();
        PersonDto dto2Decrypted = new PersonDto();
        PersonDto dto3 = new PersonDto();
        PersonDto dto3Decrypted = new PersonDto();
        when(mockEncryptionService.decrypt(dto1)).thenReturn(dto1Decrypted);
        when(mockEncryptionService.decrypt(dto2)).thenReturn(dto2Decrypted);
        when(mockEncryptionService.decrypt(dto3)).thenReturn(dto3Decrypted);
        List<PersonDto> persons = Arrays.asList(dto1, dto2, dto3);
        List<PersonDto> decryptedPersons = Arrays.asList(dto1Decrypted, dto2Decrypted, dto3Decrypted);
        List<NfcRelationDto> relations = Arrays.asList(relation(), relation(), relation());
        when(mockSynchronizationService.getLastSynchronizationDate()).thenReturn(lastSynchronizationDate);
        when(mockPersonRemoteService.getModified(lastSynchronizationDate)).thenReturn(persons);
        when(mockAppearanceRemoteService.getModified(lastSynchronizationDate)).thenReturn(appearances);
        when(mockRelationRemoteService.getModified(lastSynchronizationDate)).thenReturn(relations);
        synchronizer.synchronizeAppearancesAndNfcAutomatically();

        verify(mockUpdateLocalService).updateAppearances(appearances);
        verify(mockUpdateLocalService).updateRelations(relations);
        verify(mockUpdateLocalService, times(0)).updatePersons(persons);
        verify(mockUpdateLocalService).updatePersons(decryptedPersons);

        verify(mockUpdateRemoteService).updateAppearances();
        verify(mockUpdateRemoteService).updateRelations();
        verify(mockSynchronizationService).onSynchronizationFinished(any(Date.class));
    }

    @Test
    public void when_synch_failed_then_synchronization_is_not_persisted(){
        Date lastSynchronizationDate = null;
        when(mockSynchronizationService.getLastSynchronizationDate()).thenReturn(lastSynchronizationDate);
        when(mockPersonRemoteService.getModified(lastSynchronizationDate)).thenThrow(new RuntimeException());

        synchronizer.synchronizeAppearancesAndNfcAutomatically();

        verify(mockSynchronizationService, never()).onSynchronizationFinished(any(Date.class));
    }

    @Test
    public void manual_synchronization_works_correctly(){
        Date lastSynchronizationDate = null;
        when(mockPersonSynchronizationService.getLastSynchronizationDate()).thenReturn(null);
        PersonDto dto1 = new PersonDto();
        PersonDto dto1Decrypted = new PersonDto();
        when(mockEncryptionService.decrypt(dto1)).thenReturn(dto1Decrypted);
        when(mockPersonRemoteService.getModified(lastSynchronizationDate)).thenReturn(Arrays.asList(dto1));

        synchronizer.synchronizePersonsManually();

        verify(mockUpdateLocalService).updatePersons(Arrays.asList(dto1Decrypted));
        verify(mockPersonSynchronizationService).onSynchronizationFinished(any(Date.class), any(Integer.class));
    }

    private AppearanceDto appearance(){
        return new AppearanceDto(new Date(), TestUtils.randomString(), "RegistrationDay1");
    }

    private NfcRelationDto relation(){
        return new NfcRelationDto(TestUtils.randomString(), TestUtils.randomString());
    }
}
