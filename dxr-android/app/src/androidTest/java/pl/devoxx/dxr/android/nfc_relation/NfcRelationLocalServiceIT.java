package pl.devoxx.dxr.android.nfc_relation;

import java.util.Arrays;
import java.util.List;

import pl.devoxx.dxr.android.TestUtils;
import pl.devoxx.dxr.android.core.ServerAwareLocalService;
import pl.devoxx.dxr.android.core.ServerAwareServiceIT;
import pl.devoxx.dxr.api.results.CreateRelationResult;

/**
 * Created by wilk on 14/06/15.
 */
public class NfcRelationLocalServiceIT extends ServerAwareServiceIT<NfcRelation> {

    private NfcRelationLocalService service;
    private String nfcTagId;
    private String userId;

    @Override
    protected ServerAwareLocalService<NfcRelation> getService() {
        return service;
    }

    @Override
    protected NfcRelation newCleanEntity() {
        return new NfcRelation();
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        service = new NfcRelationLocalService(applicationConfigService, dbHelper.getNfcRelationDao());
        nfcTagId = TestUtils.randomString();
        userId = TestUtils.randomString();
    }

    public void test_when_relations_are_saved_then_they_can_be_fetched_later(){
        String userId = TestUtils.randomString();
        NfcRelation relation = new NfcRelation();
        relation.setPersistedOnServer(true);
        relation.setUserId(userId);
        relation.setNfcTagId(nfcTagId);

        CreateRelationResult createResult = service.save(relation);
        assertEquals(createResult, CreateRelationResult.SUCCESS);

        NfcRelation result = service.findByNfcTagId(nfcTagId);

        assertEquals(result.getNfcTagId(), nfcTagId);
        assertEquals(result.getUserId(), userId);
        assertTrue(result.isPersistedOnServer());
    }

    public void test_when_there_are_relations_not_persisted_on_server_then_return_them(){
        NfcRelation notPersisted1 = relation(TestUtils.randomString(), TestUtils.randomString(), false);
        NfcRelation notPersisted2 = relation(TestUtils.randomString(), TestUtils.randomString(), false);
        NfcRelation persisted = relation(TestUtils.randomString(), TestUtils.randomString(), true);

        List<CreateRelationResult> results = Arrays.asList(
                service.save(notPersisted1), service.save(notPersisted2),
                service.save(persisted));

        for(CreateRelationResult result : results){
            assertEquals(result, CreateRelationResult.SUCCESS);
        }

        List<NfcRelation> result = service.getAllRelationsNotPersistedOnServer();

        assertEquals(2, result.size());
        assertTrue(result.contains(notPersisted1));
        assertTrue(result.contains(notPersisted2));
    }

    public void test_if_update_works(){
        NfcRelation notPersisted = relation(userId, nfcTagId, false);

        CreateRelationResult result = service.save(notPersisted);
        assertEquals(result, CreateRelationResult.SUCCESS);

        NfcRelation persisted = service.findByNfcTagId(nfcTagId);
        assertFalse(persisted.isPersistedOnServer());

        persisted.setPersistedOnServer(true);
        service.update(persisted);

        NfcRelation afterUpdate = service.findByNfcTagId(nfcTagId);
        assertTrue(afterUpdate.isPersistedOnServer());
    }

    public void test_when_relation_already_exists_then_throw_exception(){
        NfcRelation existing = relation(userId, nfcTagId, true);

        CreateRelationResult result = service.save(existing);
        assertEquals(result, CreateRelationResult.SUCCESS);

        NfcRelation duplicate = relation(userId, nfcTagId, true);

        CreateRelationResult secondResult = service.save(duplicate);
        assertEquals(secondResult, CreateRelationResult.RELATION_ALREADY_EXISTS);
    }

    public void test_when_tag_already_has_another_relation_then_throw_exception(){
        NfcRelation existing = relation(userId, nfcTagId, true);

        CreateRelationResult result = service.save(existing);
        assertEquals(result, CreateRelationResult.SUCCESS);

        NfcRelation secondRelationForTheSameTag = relation(TestUtils.randomString(), nfcTagId, true);

        CreateRelationResult secondResult = service.save(secondRelationForTheSameTag);
        assertEquals(secondResult, CreateRelationResult.TAG_ALREADY_HAS_ANOTHER_RELATION);
    }

    public void test_when_user_already_connected_to_some_tag_then_he_can_be_connected_to_another_tag(){
        NfcRelation existing = relation(userId, nfcTagId, true);

        service.save(existing);

        NfcRelation secondRelationForTheSameUser = relation(userId, TestUtils.randomString(), true);

        service.save(secondRelationForTheSameUser);
    }

    public void test_when_there_already_is_appearance_at_the_same_time_then_exists_return_true(){
        NfcRelation persistedLocally = relation(userId, nfcTagId, false);

        service.save(persistedLocally);

        assertTrue(service.existsRelation(userId, nfcTagId));
        assertFalse(service.existsRelation(userId, "notExistingTagId"));
        assertFalse(service.existsRelation("notExistingUserId", nfcTagId));
        assertFalse(service.existsRelation("notExistingUserId", "notExistingTagId"));
    }

    private NfcRelation relation(String userId, String nfcTagId, boolean persistedOnServer){
        NfcRelation app = new NfcRelation();
        app.setUserId(userId);
        app.setNfcTagId(nfcTagId);
        app.setPersistedOnServer(persistedOnServer);
        return app;
    }
}
