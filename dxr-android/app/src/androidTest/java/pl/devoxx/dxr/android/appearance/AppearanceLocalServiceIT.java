package pl.devoxx.dxr.android.appearance;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.List;

import pl.devoxx.dxr.android.TestUtils;
import pl.devoxx.dxr.android.core.ServerAwareEntity;
import pl.devoxx.dxr.android.core.ServerAwareLocalService;
import pl.devoxx.dxr.android.core.ServerAwareServiceIT;

/**
 * Created by wilk on 22/03/15.
 */
public class AppearanceLocalServiceIT extends ServerAwareServiceIT{

    private static final String REGISTRATION_DAY_2 = "RegistrationDay2";
    private static final String REGISTRATION_DAY_1 = "RegistrationDay1";
    private AppearanceLocalService service;
    private String userId;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        service = new AppearanceLocalService(applicationConfigService, dbHelper.getAppearanceDao());
        userId = TestUtils.randomString();
    }

    public void test_when_appearances_are_saved_then_they_can_be_fetched_later(){
        Date now = new Date();

        Appearance appearance = new Appearance();
        appearance.setDate(now);
        appearance.setPersistedOnServer(true);
        appearance.setUserId(userId);
        appearance.setAppearanceTypeName(REGISTRATION_DAY_2);

        service.create(appearance);

        List<Appearance> result = service.getAllUserAppearances(userId, REGISTRATION_DAY_2);

        assertEquals(result.size(), 1);
        Appearance resultAppearance = result.get(0);
        assertEquals(resultAppearance.getDate(), now);
        assertEquals(resultAppearance.getUserId(), userId);
        assertTrue(resultAppearance.isPersistedOnServer());
        assertEquals(resultAppearance.getAppearanceTypeName(), REGISTRATION_DAY_2);
        assertEquals(applicationConfigService.getServerUrl(), appearance.getServerUrl());
    }

    public void test_if_all_user_appearances_are_returned_and_in_correct_order(){
        Date now = new Date();
        Date twoDaysAgo = new DateTime().minusDays(2).toDate();
        Date fiveDaysAgo = new DateTime().minusDays(5).toDate();

        Appearance firstAppearance = appearance(userId, fiveDaysAgo, false, REGISTRATION_DAY_1);
        Appearance secondAppearance = appearance(userId, twoDaysAgo, false, REGISTRATION_DAY_1);
        Appearance thirdAppearance = appearance(userId, now, false, REGISTRATION_DAY_1);

        service.create(secondAppearance);
        service.create(firstAppearance);
        service.create(thirdAppearance);


        List<Appearance> result = service.getAllUserAppearances(userId, REGISTRATION_DAY_1);

        assertEquals(3, result.size());

        assertEquals(thirdAppearance.getId(), result.get(0).getId());
        assertEquals(secondAppearance.getId(), result.get(1).getId());
        assertEquals(firstAppearance.getId(), result.get(2).getId());
    }

    public void test_when_user_has_many_types_of_appearances_return_correct_ones(){
        Date now = new Date();
        Date twoDaysAgo = new DateTime().minusDays(2).toDate();
        Date fiveDaysAgo = new DateTime().minusDays(5).toDate();

        Appearance firstAppearance = appearance(userId, fiveDaysAgo, false, REGISTRATION_DAY_2);
        Appearance secondAppearance = appearance(userId, twoDaysAgo, false, REGISTRATION_DAY_1);
        Appearance thirdAppearance = appearance(userId, now, false, REGISTRATION_DAY_2);

        service.create(secondAppearance);
        service.create(firstAppearance);
        service.create(thirdAppearance);


        List<Appearance> result = service.getAllUserAppearances(userId, REGISTRATION_DAY_2);

        assertEquals(2, result.size());
        assertTrue(result.contains(firstAppearance));
        assertTrue(result.contains(thirdAppearance));
    }

    public void test_when_there_are_appearances_not_persisted_on_server_then_return_them(){
        DateTime now = new DateTime();
        Appearance notPersisted1 = appearance(userId, now.minusMinutes(5).toDate(), false, REGISTRATION_DAY_1);
        Appearance notPersisted2 = appearance(userId, now.minusMinutes(10).toDate(), false, REGISTRATION_DAY_1);
        Appearance persisted = appearance(userId, now.minusMinutes(15).toDate(), true, REGISTRATION_DAY_1);

        service.create(notPersisted1);
        service.create(notPersisted2);
        service.create(persisted);

        List<Appearance> result = service.getAllAppearancesNotPersistedOnServer();

        assertEquals(2, result.size());
        assertTrue(result.contains(notPersisted1));
        assertTrue(result.contains(notPersisted2));
    }

    public void test_when_there_already_is_appearance_at_the_same_time_then_exists_return_true(){
        Date now = new Date();
        Appearance persistedLocally = appearance(userId, now, true, REGISTRATION_DAY_1);

        service.create(persistedLocally);

        assertTrue(service.existsAppearance(userId, now, REGISTRATION_DAY_1));
        assertFalse(service.existsAppearance(userId, now, REGISTRATION_DAY_2));
        assertFalse(service.existsAppearance(userId, new Date(), REGISTRATION_DAY_1));
        assertFalse(service.existsAppearance("nonExistentUserId", now, REGISTRATION_DAY_1));
    }

    private Appearance appearance(String userId, Date date, boolean persistedOnServer, String appearanceTypeName){
        Appearance app = new Appearance();
        app.setUserId(userId);
        app.setDate(date);
        app.setPersistedOnServer(persistedOnServer);
        app.setAppearanceTypeName(appearanceTypeName);
        return app;
    }

    @Override
    protected ServerAwareLocalService getService() {
        return service;
    }

    @Override
    protected ServerAwareEntity newCleanEntity() {
        return new Appearance();
    }
}
