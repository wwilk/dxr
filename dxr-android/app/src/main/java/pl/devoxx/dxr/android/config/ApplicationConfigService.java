package pl.devoxx.dxr.android.config;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.sql.SQLException;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import pl.devoxx.dxr.android.appearanceType.AppearanceType;

/**
 * Created by wilk on 14/04/15.
 */
public class ApplicationConfigService implements ServerUrlProvider {

    private static final Integer ONLY_RECORD_ID = 1;
    public static final AppearanceType DEFAULT_APPEARANCE_TYPE = new AppearanceType("DEFAULT", false, null);
    public static final String DEFAULT_SERVER_URL = "http://localhost:8080/";

    private static final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private static final Lock readLock = rwl.readLock();
    private static final Lock writeLock = rwl.writeLock();

    private final RuntimeExceptionDao<ApplicationConfig, Integer> applicationConfigDao;
    private final RuntimeExceptionDao<AppearanceType, Integer> appearanceTypeDao;

    public ApplicationConfigService(RuntimeExceptionDao<ApplicationConfig, Integer> applicationConfigDao,
                                    RuntimeExceptionDao<AppearanceType, Integer> appearanceTypeDao) {
        this.applicationConfigDao = applicationConfigDao;
        this.appearanceTypeDao = appearanceTypeDao;
    }

    public ApplicationConfig getConfig(){
        readLock.lock();
        try {
            ApplicationConfig config = applicationConfigDao.queryForId(ONLY_RECORD_ID);
            if(config != null){
                return config;
            }
        } finally{
            readLock.unlock();
        }

        writeLock.lock();
        try {
            ApplicationConfig config = applicationConfigDao.queryForId(ONLY_RECORD_ID);
            if(config == null){
                config = new ApplicationConfig();
                config.setId(ONLY_RECORD_ID);
                config.setPhoneId(new Random(System.nanoTime()).nextInt());
                appearanceTypeDao.create(DEFAULT_APPEARANCE_TYPE);
                config.setAppearanceType(DEFAULT_APPEARANCE_TYPE);
                config.setServerUrl(DEFAULT_SERVER_URL);
                applicationConfigDao.create(config);
            }
            PhoneIdHolder.set(config.getPhoneId());
            return config;
        } finally{
            writeLock.unlock();
        }
    }

    public String getServerUrl(){
        return getConfig().getServerUrl();
    }

    public void updateAppearanceType(AppearanceType newAppearanceType){
        if(newAppearanceType == null){
            throw new IllegalArgumentException("appearanceType can't be null");
        }
        writeLock.lock();
        try {
            ApplicationConfig config = getConfig();
            config.setAppearanceType(newAppearanceType);
            applicationConfigDao.update(config);
        } finally{
            writeLock.unlock();
        }
    }

    public void setAppearanceTypeToDefault(){
        writeLock.lock();
        try {
            AppearanceType type = appearanceTypeDao.queryBuilder().where().eq("name", DEFAULT_APPEARANCE_TYPE.getName())
                    .queryForFirst();

            if(type == null) {
                appearanceTypeDao.create(DEFAULT_APPEARANCE_TYPE);
            }

            updateAppearanceType(type);
        } catch(SQLException e) {
            throw new RuntimeException(e);
        } finally{
            writeLock.unlock();
        }
    }

    public void updateServerUrl(String serverUrl){
        if(serverUrl == null){
            throw new IllegalArgumentException("serverUrl can't be null");
        }
        writeLock.lock();
        try {
            ApplicationConfig config = getConfig();
            config.setServerUrl(serverUrl);
            applicationConfigDao.update(config);
        } finally{
            writeLock.unlock();
        }
    }
}
