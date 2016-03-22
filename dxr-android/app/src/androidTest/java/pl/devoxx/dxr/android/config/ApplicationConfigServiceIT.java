package pl.devoxx.dxr.android.config;

import pl.devoxx.dxr.android.DatabaseIT;
import pl.devoxx.dxr.android.appearanceType.AppearanceType;

/**
 * Created by wilk on 14/04/15.
 */
public class ApplicationConfigServiceIT extends DatabaseIT{

    public static final String DEFAULT = "DEFAULT";
    public static final String SABRE = "Sabre";
    private ApplicationConfigService configService;

    @Override
    public void setUp() throws Exception{
        super.setUp();
        configService = new ApplicationConfigService(dbHelper.getApplicationConfigDao(),
                dbHelper.getAppearanceTypeDao());
    }

    public void test_when_there_is_no_config_then_create_it(){
        assertTrue(dbHelper.getApplicationConfigDao().queryForAll().isEmpty());

        ApplicationConfig config = configService.getConfig();

        assertEquals(config.getServerUrl(), ApplicationConfigService.DEFAULT_SERVER_URL); // default value
        assertEquals(config.getAppearanceType(), ApplicationConfigService.DEFAULT_APPEARANCE_TYPE);
    }

    public void test_when_default_config_is_created_first_time_then_default_appearance_type_is_persisted(){
        assertTrue(dbHelper.getApplicationConfigDao().queryForAll().isEmpty());

        ApplicationConfig config = configService.getConfig();

        assertEquals(config.getAppearanceType().getName(), DEFAULT); // default value

        ApplicationConfig configAtSecondAccess = configService.getConfig();

        assertEquals(configAtSecondAccess.getServerUrl(), ApplicationConfigService.DEFAULT_SERVER_URL); // default value
        assertEquals(configAtSecondAccess.getAppearanceType(), ApplicationConfigService.DEFAULT_APPEARANCE_TYPE);
    }

    public void test_when_get_is_called_multiple_times_then_it_always_returns_the_same_instance(){
        ApplicationConfig config = configService.getConfig();
        ApplicationConfig config2 = configService.getConfig();
        ApplicationConfig config3 = configService.getConfig();

        assertEquals(config.getId(), config2.getId());
        assertEquals(config.getId(), config3.getId());
    }

    public void test_when_appearance_type_is_updated_then_next_get_call_returns_updated_type(){
        ApplicationConfig config = configService.getConfig();
        assertEquals(config.getAppearanceType().getName(), DEFAULT);

        AppearanceType newType = new AppearanceType(SABRE, false, null);
        dbHelper.getAppearanceTypeDao().create(newType);

        configService.updateAppearanceType(newType);

        ApplicationConfig updated = configService.getConfig();
        assertEquals(updated.getAppearanceType().getName(), SABRE);
        assertEquals(updated.getId(), config.getId());
    }

    public void test_when_server_url_is_updated_then_next_get_call_returns_updated_url(){
        ApplicationConfig config = configService.getConfig();
        assertEquals(config.getServerUrl(), ApplicationConfigService.DEFAULT_SERVER_URL);

        String newValue = "http://33degree.herokuapp.com/";
        configService.updateServerUrl(newValue);

        ApplicationConfig updated = configService.getConfig();
        assertEquals(updated.getServerUrl(), newValue);
        assertEquals(updated.getId(), config.getId());
    }

    public void test_when_appearance_type_is_set_to_default_again_then_it_can_be_accessed(){
        ApplicationConfig config = configService.getConfig();
        assertEquals(config.getAppearanceType(), ApplicationConfigService.DEFAULT_APPEARANCE_TYPE);

        AppearanceType newType = new AppearanceType(SABRE, false, null);
        dbHelper.getAppearanceTypeDao().create(newType);

        configService.updateAppearanceType(newType);

        assertEquals(configService.getConfig().getAppearanceType(), newType);

        configService.setAppearanceTypeToDefault();

        assertEquals(configService.getConfig().getAppearanceType(), ApplicationConfigService.DEFAULT_APPEARANCE_TYPE);
    }
}
