package pl.devoxx.dxr.android.synchronization.person;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.util.Date;

import pl.devoxx.dxr.android.config.ServerUrlProvider;
import pl.devoxx.dxr.android.core.ServerAwareLocalService;

/**
 * Created by wilk on 07/12/15.
 */
public class PersonSynchronizationService extends ServerAwareLocalService<PersonSynchronization>{

    public PersonSynchronizationService(RuntimeExceptionDao<PersonSynchronization, Integer> dao,
                                        ServerUrlProvider serverUrlProvider) {
        super(serverUrlProvider, dao);
    }

    public void onSynchronizationFinished(Date date, int numberOfPersonsUpdated){
        PersonSynchronization synchronization = new PersonSynchronization();
        synchronization.setDate(date);
        synchronization.setSynchronizedPeopleCount(numberOfPersonsUpdated);
        super.create(synchronization);
    }

    public PersonSynchronization getLastSynchronization(){
        QueryBuilder<PersonSynchronization, Integer> query =  dao.queryBuilder().orderBy("date", false);
        return dao.queryForFirst(prepareWithServerUrl(query));
    }

    public Date getLastSynchronizationDate(){
        PersonSynchronization lastSync = getLastSynchronization();
        return lastSync == null ? null : lastSync.getDate();
    }
}
