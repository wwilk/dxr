package pl.devoxx.dxr.android.appearance;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import pl.devoxx.dxr.android.config.ServerUrlProvider;
import pl.devoxx.dxr.android.core.ServerAwareLocalService;

/**
 * Created by wilk on 22/03/15.
 */
public class AppearanceLocalService extends ServerAwareLocalService<Appearance>{

    public AppearanceLocalService(ServerUrlProvider serverUrlProvider, RuntimeExceptionDao<Appearance, Integer> dao){
        super(serverUrlProvider, dao);
    }

    public List<Appearance> getAllUserAppearances(String userId, String appearanceTypeName){
        try {
            Where<Appearance, Integer> query = dao.queryBuilder().orderBy("date", false).where().eq("userId", userId)
                    .and().eq("appearanceTypeName", appearanceTypeName);
            return dao.query(prepareWithServerUrl(query));
        } catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public boolean existsAppearance(String userId, Date date, String appearanceTypeName){
        try{
            Where<Appearance, Integer> query = dao.queryBuilder().where()
                    .eq("userId", userId).and()
                    .eq("date", date).and()
                    .eq("appearanceTypeName", appearanceTypeName);
            return dao.queryForFirst(prepareWithServerUrl(query)) != null;
        } catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public List<Appearance> getAllAppearancesNotPersistedOnServer(){
        try {
            Where<Appearance, Integer> query = dao.queryBuilder().where().eq("persistedOnServer", false);
            return dao.query(prepareWithServerUrl(query));
        } catch(SQLException e){
            throw new RuntimeException(e);
        }
    }
}
