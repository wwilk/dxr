package pl.devoxx.dxr.android.synchronization;

import org.joda.time.DateTime;

import java.util.Date;

import pl.devoxx.dxr.android.core.ServerAwareLocalService;
import pl.devoxx.dxr.android.core.ServerAwareServiceIT;

/**
 * Created by wilk on 12/04/15.
 */
public class SynchronizationServiceIT extends ServerAwareServiceIT<Synchronization>{

    private SynchronizationService synchronizationService;

    @Override
    protected ServerAwareLocalService<Synchronization> getService() {
        return synchronizationService;
    }

    @Override
    protected Synchronization newCleanEntity() {
        return new Synchronization();
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        synchronizationService = new SynchronizationService(dbHelper.getSynchronizationDao(), applicationConfigService);
    }

    public void test_when_there_was_no_synchronization_then_return_null(){
        Synchronization result = synchronizationService.getLastSynchronization();
        Date lastSynchronizationDate = synchronizationService.getLastSynchronizationDate();
        assertNull(result);
        assertNull(lastSynchronizationDate);
    }

    public void test_when_there_exist_multiple_synchronizations_then_return_last_one(){
        DateTime now = new DateTime();

        synchronizationService.onSynchronizationFinished(now.minusMinutes(10).toDate());
        synchronizationService.onSynchronizationFinished(now.minusMinutes(5).toDate());
        synchronizationService.onSynchronizationFinished(now.minusMinutes(15).toDate());

        Synchronization result = synchronizationService.getLastSynchronization();
        Date lastSynchronizationDate = synchronizationService.getLastSynchronizationDate();

        assertEquals(now.minusMinutes(5).toDate(), result.getDate());
        assertEquals(now.minusMinutes(5).toDate(), lastSynchronizationDate);
    }
}
