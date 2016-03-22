package pl.devoxx.dxr.android;

import android.app.Application;
import android.test.ApplicationTestCase;

import pl.devoxx.dxr.android.config.ApplicationConfigService;
import pl.devoxx.dxr.android.database.DbHelper;
import pl.devoxx.dxr.android.database.DbManager;

/**
 * Created by wilk on 22/03/15.
 */
public abstract class DatabaseIT extends ApplicationTestCase<Application> {

    private static final String TEST_DATABASE_NAME = "dxr-database-test.db";
    private static final int version = 10;
    protected DbHelper dbHelper;
    protected ApplicationConfigService applicationConfigService;

    public DatabaseIT() {
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception {
        dbHelper = DbManager.getInstance(getContext(), TEST_DATABASE_NAME, version);
        dbHelper.createAllTables();
        applicationConfigService = new ApplicationConfigService(dbHelper.getApplicationConfigDao(), dbHelper.getAppearanceTypeDao());
    }

    @Override
    protected void tearDown() throws Exception {
        dbHelper.dropAllTables();
        dbHelper = null;
        DbManager.release(TEST_DATABASE_NAME);
    }
}
