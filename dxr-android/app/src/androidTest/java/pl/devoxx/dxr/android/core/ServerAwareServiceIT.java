package pl.devoxx.dxr.android.core;

import java.util.List;

import pl.devoxx.dxr.android.DatabaseIT;

/**
 * Created by wilk on 17/12/15.
 */
public abstract class ServerAwareServiceIT<E extends ServerAwareEntity> extends DatabaseIT{

    protected abstract ServerAwareLocalService<E> getService();

    protected abstract E newCleanEntity();

    private final String differentThanCurrentServerUrl = "https://differentServerUrl:4454";

    @Override
    public void setUp() throws Exception{
        super.setUp();
        assertNotNull(applicationConfigService.getServerUrl());
        assertFalse(differentThanCurrentServerUrl.equals(applicationConfigService.getServerUrl()));
    }

    public void test_when_entity_created_then_its_server_url_is_set_to_current_one(){
        E entity = persistEntityWithServerUrl(null);

        assertEquals(applicationConfigService.getServerUrl(), entity.getServerUrl());
    }

    public void test_when_entity_created_and_its_server_url_is_not_null_then_dont_change_it(){
        E entityWithAlreadySetServerUrl = persistEntityWithServerUrl(differentThanCurrentServerUrl);

        assertEquals(differentThanCurrentServerUrl, entityWithAlreadySetServerUrl.getServerUrl());
    }

    public void test_when_entity_updated_then_its_server_url_stays_the_same(){
        E entity = persistEntityWithServerUrl(differentThanCurrentServerUrl);

        getService().update(entity);

        assertEquals(differentThanCurrentServerUrl, entity.getServerUrl());
    }

    public void test_when_entities_from_different_servers_then_findAll_and_countAll_return_only_the_ones_from_current_server(){
        applicationConfigService.getServerUrl();

        E differentServerEntity = persistEntityWithServerUrl(differentThanCurrentServerUrl);
        E otherServerEntity = persistEntityWithServerUrl("http://otherserver:343432/");
        E currentServerEntity = persistEntityWithServerUrl(applicationConfigService.getServerUrl());
        E currentServerEntity2 = persistEntityWithServerUrl(applicationConfigService.getServerUrl());

        List<E> all = getService().findAll();
        long allCount = getService().countAll();

        assertEquals(2, all.size());
        assertEquals(2, allCount);
        assertTrue(all.contains(currentServerEntity));
        assertTrue(all.contains(currentServerEntity2));
    }

    private E persistEntityWithServerUrl(String serverUrl){
        E entity = newCleanEntity();
        entity.setServerUrl(serverUrl);
        getService().create(entity);
        return entity;
    }
}
