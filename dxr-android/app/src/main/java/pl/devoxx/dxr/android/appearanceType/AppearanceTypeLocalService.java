package pl.devoxx.dxr.android.appearanceType;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.List;

import pl.devoxx.dxr.android.config.ServerUrlProvider;
import pl.devoxx.dxr.android.core.ServerAwareLocalService;

/**
 * Created by wilk on 05/12/15.
 */
public class AppearanceTypeLocalService extends ServerAwareLocalService<AppearanceType>{

    public AppearanceTypeLocalService(ServerUrlProvider serverUrlProvider,
                                      RuntimeExceptionDao<AppearanceType, Integer> dao){
        super(serverUrlProvider, dao);
    }

    public void replaceAll(List<AppearanceType> newTypes){
        deleteAllFromThisServer();
        for(AppearanceType type : newTypes) {
            super.create(type);
        }
    }

    private void deleteAllFromThisServer(){
        List<AppearanceType> all = findAll();
        for(AppearanceType each : all){
            dao.delete(each);
        }
    }

}
