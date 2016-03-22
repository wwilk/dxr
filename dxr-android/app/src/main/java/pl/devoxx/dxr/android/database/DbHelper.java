package pl.devoxx.dxr.android.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import pl.devoxx.dxr.android.appearance.Appearance;
import pl.devoxx.dxr.android.appearanceType.AppearanceType;
import pl.devoxx.dxr.android.config.ApplicationConfig;
import pl.devoxx.dxr.android.nfc_relation.NfcRelation;
import pl.devoxx.dxr.android.registration.Person;
import pl.devoxx.dxr.android.synchronization.Synchronization;
import pl.devoxx.dxr.android.synchronization.person.PersonSynchronization;

/**
 * Created by wilk on 21/03/15.
 */
public class DbHelper extends OrmLiteSqliteOpenHelper {

    private final int version;

    private RuntimeExceptionDao<Appearance, Integer> appearanceDao;
    private RuntimeExceptionDao<Person, Integer> personDao;
    private RuntimeExceptionDao<Synchronization, Integer> synchronizationDao;
    private RuntimeExceptionDao<ApplicationConfig, Integer> applicationConfigDao;
    private RuntimeExceptionDao<NfcRelation, Integer> nfcRelationDao;
    private RuntimeExceptionDao<AppearanceType, Integer> appearanceTypeDao;
    private RuntimeExceptionDao<PersonSynchronization, Integer> personSynchronizationDao;

    public DbHelper(Context context, String databaseName, int version) {
        super(context, databaseName, null, version);
        this.version = version;
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(DbHelper.class.getName(), "Creating database");
            createAllTables();
        } catch (SQLException e) {
            Log.e(DbHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(DbHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion);
            dropAllTables();
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(DbHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    public void createAllTables() throws SQLException{
        TableUtils.createTableIfNotExists(connectionSource, Appearance.class);
        TableUtils.createTableIfNotExists(connectionSource, Person.class);
        TableUtils.createTableIfNotExists(connectionSource, Synchronization.class);
        TableUtils.createTableIfNotExists(connectionSource, ApplicationConfig.class);
        TableUtils.createTableIfNotExists(connectionSource, NfcRelation.class);
        TableUtils.createTableIfNotExists(connectionSource, AppearanceType.class);
        TableUtils.createTableIfNotExists(connectionSource, PersonSynchronization.class);
    }

    public void dropAllTables() throws SQLException{
        TableUtils.dropTable(connectionSource, Appearance.class, true);
        TableUtils.dropTable(connectionSource, Person.class, true);
        TableUtils.dropTable(connectionSource, Synchronization.class, true);
        TableUtils.dropTable(connectionSource, ApplicationConfig.class, true);
        TableUtils.dropTable(connectionSource, NfcRelation.class, true);
        TableUtils.dropTable(connectionSource, AppearanceType.class, true);
        TableUtils.dropTable(connectionSource, PersonSynchronization.class, true);
    }

    public RuntimeExceptionDao<Appearance, Integer> getAppearanceDao() {
        if (appearanceDao == null) {
            appearanceDao = getRuntimeExceptionDao(Appearance.class);
        }
        return appearanceDao;
    }

    public RuntimeExceptionDao<Person, Integer> getPersonDao() {
        if (personDao == null) {
            personDao = getRuntimeExceptionDao(Person.class);
        }
        return personDao;
    }

    public RuntimeExceptionDao<Synchronization, Integer> getSynchronizationDao() {
        if (synchronizationDao == null) {
            synchronizationDao = getRuntimeExceptionDao(Synchronization.class);
        }
        return synchronizationDao;
    }

    public RuntimeExceptionDao<ApplicationConfig, Integer> getApplicationConfigDao() {
        if (applicationConfigDao == null) {
            applicationConfigDao = getRuntimeExceptionDao(ApplicationConfig.class);
        }
        return applicationConfigDao;
    }

    public RuntimeExceptionDao<NfcRelation, Integer> getNfcRelationDao() {
        if (nfcRelationDao == null) {
            nfcRelationDao = getRuntimeExceptionDao(NfcRelation.class);
        }
        return nfcRelationDao;
    }

    public RuntimeExceptionDao<AppearanceType, Integer> getAppearanceTypeDao(){
        if(appearanceTypeDao == null){
            appearanceTypeDao = getRuntimeExceptionDao(AppearanceType.class);
        }
        return appearanceTypeDao;
    }

    public RuntimeExceptionDao<PersonSynchronization, Integer> getPersonSynchronizationDao(){
        if(personSynchronizationDao == null){
            personSynchronizationDao = getRuntimeExceptionDao(PersonSynchronization.class);
        }
        return personSynchronizationDao;
    }

    @Override
    public void close() {
        super.close();
        appearanceDao = null;
        personDao = null;
        synchronizationDao = null;
        applicationConfigDao = null;
        nfcRelationDao = null;
        appearanceTypeDao = null;
    }

    public int getVersion(){
        return version;
    }

}
