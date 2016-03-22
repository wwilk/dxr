package pl.devoxx.dxr.android.registration;

import pl.devoxx.dxr.android.TestUtils;
import pl.devoxx.dxr.android.appearance.AppearanceLocalService;
import pl.devoxx.dxr.android.core.ServerAwareLocalService;
import pl.devoxx.dxr.android.core.ServerAwareServiceIT;
import pl.devoxx.dxr.android.nfc_relation.NfcRelation;
import pl.devoxx.dxr.android.nfc_relation.NfcRelationLocalService;
import pl.devoxx.dxr.api.person.PersonDto;
import pl.devoxx.dxr.api.person.Status;

/**
 * Created by wilk on 22/03/15.
 */
public class PersonLocalServiceIT extends ServerAwareServiceIT<Person>{

    private PersonLocalService personLocalService;
    private NfcRelationLocalService nfcRelationLocalService;

    @Override
    protected ServerAwareLocalService<Person> getService() {
        return personLocalService;
    }

    @Override
    protected Person newCleanEntity() {
        return new Person();
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        nfcRelationLocalService = new NfcRelationLocalService(applicationConfigService, dbHelper.getNfcRelationDao());
        personLocalService = new PersonLocalService(dbHelper.getPersonDao(), new AppearanceLocalService(applicationConfigService, dbHelper.getAppearanceDao()),
                nfcRelationLocalService, applicationConfigService);
    }

    public void test_if_person_doesnt_exist_then_findByUserId_returns_null(){
        assertNull(personLocalService.findByUserId("not existing id"));
        assertNull(personLocalService.findByUserId("not existing id", "Registration"));
    }

    public void test_if_relation_doesnt_exist_then_findByNfcTagId_returns_null(){
        assertNull(personLocalService.findByNfcTagId("not existing id", "Registration"));
    }

    public void test_if_person_is_persisted_correctly(){
        String userId = "userIdd343";
        String firstName = "first";
        String lastName = "last";
        String email = "email@test.pl";
        Status status = Status.Canceled;

        Person person = new Person();
        person.setUserId(userId);
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setEmail(email);
        person.setRegistrationStatus(status);

        personLocalService.create(person);

        Person result = personLocalService.findByUserId(userId, "RegistrationDay1");

        assertEquals(email, result.getEmail());
        assertEquals(userId, result.getUserId());
        assertEquals(firstName, result.getFirstName());
        assertEquals(lastName, result.getLastName());
        assertEquals(status, result.getRegistrationStatus());
    }

    public void test_if_conversion_to_dto_and_from_dto_is_correct(){
        String userId = "userIdd343";
        String firstName = "first";
        String lastName = "last";
        String email = "email@test.pl";
        Status status = Status.Canceled;

        Person person = new Person();
        person.setUserId(userId);
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setEmail(email);
        person.setRegistrationStatus(status);

        PersonDto dto = person.toDto();

        assertEquals(email, dto.getEmail());
        assertEquals(userId, dto.getUserId());
        assertEquals(firstName, dto.getFirstName());
        assertEquals(lastName, dto.getLastName());
        assertEquals(status, dto.getRegistrationStatus());

        Person fromDto = new Person(dto);

        assertEquals(email, fromDto.getEmail());
        assertEquals(userId, fromDto.getUserId());
        assertEquals(firstName, fromDto.getFirstName());
        assertEquals(lastName, fromDto.getLastName());
        assertEquals(status, fromDto.getRegistrationStatus());
    }

    public void test_when_user_has_relation_then_he_is_found_by_tag_id(){
        String tagId = TestUtils.randomString();
        String userId = TestUtils.randomString();
        NfcRelation relation = new NfcRelation(userId, tagId);
        nfcRelationLocalService.save(relation);
        Person person = new Person();
        person.setUserId(userId);
        personLocalService.create(person);

        Person result = personLocalService.findByNfcTagId(tagId, "RegistrationDay2");

        assertEquals(result.getUserId(), userId);
    }

    public void test_user_can_be_found_by_his_id(){
        String userId = TestUtils.randomString();
        Person person = new Person();
        person.setUserId(userId);
        personLocalService.create(person);

        Person result = personLocalService.findByUserId(userId, "RegistrationDay2");

        assertEquals(result.getUserId(), userId);
    }
}
