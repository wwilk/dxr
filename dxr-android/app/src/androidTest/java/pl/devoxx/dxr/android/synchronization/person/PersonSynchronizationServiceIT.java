package pl.devoxx.dxr.android.synchronization.person;

import org.joda.time.DateTime;

import java.util.Date;

import pl.devoxx.dxr.android.core.ServerAwareLocalService;
import pl.devoxx.dxr.android.core.ServerAwareServiceIT;

/**
 * Created by wilk on 20/12/15.
 */
public class PersonSynchronizationServiceIT extends ServerAwareServiceIT<PersonSynchronization> {

    private PersonSynchronizationService synchronizationService;

    @Override
    protected ServerAwareLocalService<PersonSynchronization> getService() {
        return synchronizationService;
    }

    @Override
    protected PersonSynchronization newCleanEntity() {
        return new PersonSynchronization();
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        synchronizationService = new PersonSynchronizationService(dbHelper.getPersonSynchronizationDao(), applicationConfigService);
    }

    public void test_when_there_was_no_synchronization_then_return_null(){
        PersonSynchronization result = synchronizationService.getLastSynchronization();
        Date lastSynchronizationDate = synchronizationService.getLastSynchronizationDate();
        assertNull(result);
        assertNull(lastSynchronizationDate);
    }

    public void test_when_there_exist_multiple_synchronizations_then_return_last_one(){
        DateTime now = new DateTime();

        synchronizationService.onSynchronizationFinished(now.minusMinutes(10).toDate(), 1);
        synchronizationService.onSynchronizationFinished(now.minusMinutes(5).toDate(), 1);
        synchronizationService.onSynchronizationFinished(now.minusMinutes(15).toDate(), 1);

        PersonSynchronization result = synchronizationService.getLastSynchronization();
        Date lastSynchronizationDate = synchronizationService.getLastSynchronizationDate();

        assertEquals(now.minusMinutes(5).toDate(), result.getDate());
        assertEquals(now.minusMinutes(5).toDate(), lastSynchronizationDate);
    }
}
