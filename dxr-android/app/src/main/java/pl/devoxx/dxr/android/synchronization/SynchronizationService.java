package pl.devoxx.dxr.android.synchronization;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.util.Date;

import pl.devoxx.dxr.android.config.ServerUrlProvider;
import pl.devoxx.dxr.android.core.ServerAwareLocalService;

/**
 * Created by wilk on 22/03/15.
 */
public class SynchronizationService extends ServerAwareLocalService<Synchronization>{

    public SynchronizationService(RuntimeExceptionDao<Synchronization, Integer> dao,
                                  ServerUrlProvider serverUrlProvider){
        super(serverUrlProvider, dao);
    }

    public void onSynchronizationFinished(Date date){
        Synchronization synchronization = new Synchronization();
        synchronization.setDate(date);
        super.create(synchronization);
    }

    public Synchronization getLastSynchronization(){
        QueryBuilder<Synchronization, Integer> query = dao.queryBuilder().orderBy("date", false);
        return dao.queryForFirst(prepareWithServerUrl(query));
    }

    public Date getLastSynchronizationDate(){
        Synchronization lastSynchronization = getLastSynchronization();
        return lastSynchronization == null ? null : lastSynchronization.getDate();
    }
}
