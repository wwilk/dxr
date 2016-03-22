package pl.devoxx.dxr.android.appearanceType;

import java.util.ArrayList;
import java.util.List;

import pl.devoxx.dxr.android.core.ServerAwareLocalService;
import pl.devoxx.dxr.android.core.ServerAwareServiceIT;

/**
 * Created by wilk on 22/03/15.
 */
public class AppearanceTypeLocalServiceIT extends ServerAwareServiceIT<AppearanceType>{

    private AppearanceTypeLocalService service;
    private final AppearanceType REGISTRATION = new AppearanceType("Registration", true, "devoxx@devoxx.pl");
    private final AppearanceType GADGETS = new AppearanceType("Gadgets", false, null);
    private final AppearanceType DEVOXX = new AppearanceType("Devoxx", false, null);

    @Override
    protected ServerAwareLocalService<AppearanceType> getService() {
        return service;
    }

    @Override
    protected AppearanceType newCleanEntity() {
        return new AppearanceType();
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        service = new AppearanceTypeLocalService(applicationConfigService, dbHelper.getAppearanceTypeDao());
    }

    public void test_when_no_types_and_replace_is_called_then_new_list_is_saved_properly(){
        assertTrue(service.findAll().isEmpty());

        List<AppearanceType> newTypes = new ArrayList<AppearanceType>();
        newTypes.add(REGISTRATION);
        newTypes.add(GADGETS);
        newTypes.add(DEVOXX);

        String newServerUrl = "http://newServerUrl:8090/";
        applicationConfigService.updateServerUrl(newServerUrl);
        service.replaceAll(newTypes);

        List<AppearanceType> result = service.findAll();

        assertEquals(result.size(), 3);
        assertEquals(result, newTypes);
        for(AppearanceType type : result){
            assertEquals(newServerUrl, type.getServerUrl());
        }
    }

    public void test_when_types_exist_and_replace_is_called_then_new_types_are_returned_by_findAll(){
        assertTrue(service.findAll().isEmpty());

        List<AppearanceType> oldTypes = new ArrayList<AppearanceType>();
        oldTypes.add(REGISTRATION);
        oldTypes.add(GADGETS);
        oldTypes.add(DEVOXX);

        String secondServerUrl = "http://secondServerUrl.com/";
        applicationConfigService.updateServerUrl(secondServerUrl);
        service.replaceAll(oldTypes);

        List<AppearanceType> previousTypes = service.findAll();
        assertEquals(previousTypes.size(), 3);
        for(AppearanceType type : previousTypes){
            assertEquals(secondServerUrl, type.getServerUrl());
        }

        AppearanceType thirty3Degree = new AppearanceType("33Degree", true, "33degree@33degree.pl");
        AppearanceType test = new AppearanceType("Test", false, null);
        List<AppearanceType> newTypes = new ArrayList<AppearanceType>();
        newTypes.add(thirty3Degree);
        newTypes.add(test);

        String thirdServerUrl = "http://thirdserverurl.com/";
        applicationConfigService.updateServerUrl(thirdServerUrl);
        service.replaceAll(newTypes);

        List<AppearanceType> result = service.findAll();

        assertEquals(result.size(), 2);
        assertEquals(result, newTypes);
        for(AppearanceType type : newTypes){
            assertEquals(thirdServerUrl, type.getServerUrl());
        }
    }

    public void test_when_types_exist_and_replace_is_called_then_types_from_previous_url_still_exist_after_changing_url_back(){
        assertTrue(service.findAll().isEmpty());

        List<AppearanceType> oldTypes = new ArrayList<AppearanceType>();
        oldTypes.add(REGISTRATION);
        oldTypes.add(GADGETS);
        oldTypes.add(DEVOXX);

        String secondServerUrl = "http://secondServerUrl.com/";
        applicationConfigService.updateServerUrl(secondServerUrl);
        service.replaceAll(oldTypes);

        List<AppearanceType> previousTypes = service.findAll();
        assertEquals(previousTypes.size(), 3);
        for(AppearanceType type : previousTypes){
            assertEquals(secondServerUrl, type.getServerUrl());
        }

        AppearanceType thirty3Degree = new AppearanceType("33Degree", true, "33degree@33degree.pl");
        AppearanceType test = new AppearanceType("Test", false, null);
        List<AppearanceType> newTypes = new ArrayList<AppearanceType>();
        newTypes.add(thirty3Degree);
        newTypes.add(test);

        String thirdServerUrl = "http://thirdserverurl.com/";
        applicationConfigService.updateServerUrl(thirdServerUrl);
        service.replaceAll(newTypes);

        List<AppearanceType> result = service.findAll();

        assertEquals(result.size(), 2);
        assertEquals(result, newTypes);

        applicationConfigService.updateServerUrl(secondServerUrl);

        List<AppearanceType> secondServerTypes = service.findAll();

        assertEquals(previousTypes.size(), 3);
        assertEquals(previousTypes, secondServerTypes);
        for(AppearanceType type : previousTypes){
            assertEquals(secondServerUrl, type.getServerUrl());
        }
    }

    public void test_when_types_exist_and_replace_is_called_then_types_from_the_same_url_are_replaced(){
        assertTrue(service.findAll().isEmpty());

        List<AppearanceType> oldTypes = new ArrayList<AppearanceType>();
        oldTypes.add(REGISTRATION);
        oldTypes.add(GADGETS);
        oldTypes.add(DEVOXX);

        service.replaceAll(oldTypes);

        List<AppearanceType> previousTypes = service.findAll();
        assertEquals(previousTypes.size(), 3);
        for(AppearanceType type : previousTypes){
            assertEquals(applicationConfigService.getServerUrl(), type.getServerUrl());
        }

        AppearanceType thirty3Degree = new AppearanceType("33Degree", true, "33degree@33degree.pl");
        AppearanceType test = new AppearanceType("Test", false, null);
        List<AppearanceType> newTypes = new ArrayList<AppearanceType>();
        newTypes.add(thirty3Degree);
        newTypes.add(test);

        service.replaceAll(newTypes);

        List<AppearanceType> result = service.findAll();

        assertEquals(result.size(), 2);
        assertEquals(result, newTypes);
    }
}
