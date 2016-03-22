package pl.devoxx.dxr.android.synchronization;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import pl.devoxx.dxr.android.DatabaseIT;
import pl.devoxx.dxr.android.TestUtils;
import pl.devoxx.dxr.android.appearance.Appearance;
import pl.devoxx.dxr.android.appearance.AppearanceLocalService;
import pl.devoxx.dxr.android.nfc_relation.NfcRelation;
import pl.devoxx.dxr.android.nfc_relation.NfcRelationLocalService;
import pl.devoxx.dxr.android.registration.Person;
import pl.devoxx.dxr.android.registration.PersonLocalService;
import pl.devoxx.dxr.api.appearance.AppearanceDto;
import pl.devoxx.dxr.api.nfc_relation.NfcRelationDto;
import pl.devoxx.dxr.api.person.PersonDto;

/**
 * Created by wilk on 22/03/15.
 */
public class UpdateLocalServiceIT extends DatabaseIT{

    private UpdateLocalService updateLocalService;
    private AppearanceLocalService appearanceLocalService;
    private PersonLocalService personLocalService;
    private NfcRelationLocalService relationLocalService;

    @Override
    public void setUp() throws Exception{
        super.setUp();
        appearanceLocalService = new AppearanceLocalService(applicationConfigService, dbHelper.getAppearanceDao());
        relationLocalService = new NfcRelationLocalService(applicationConfigService, dbHelper.getNfcRelationDao());
        personLocalService = new PersonLocalService(dbHelper.getPersonDao(), appearanceLocalService, relationLocalService, applicationConfigService);
        updateLocalService = new UpdateLocalService(appearanceLocalService, personLocalService, relationLocalService);
    }

    public void test_if_relations_are_saved(){
        List<NfcRelationDto> relations = new ArrayList<NfcRelationDto>();
        NfcRelationDto relation = new NfcRelationDto();
        relation.setUserId("userId");
        relation.setNfcTagId("NfcTagId");
        NfcRelationDto relation2 = new NfcRelationDto();
        relation2.setUserId("user2Id");
        relation2.setNfcTagId("NfcTagId2");
        relations.add(relation);
        relations.add(relation2);

        updateLocalService.updateRelations(relations);

        List<NfcRelation> result = relationLocalService.findAll();

        assertEquals(2, result.size());
    }

    public void test_if_appearances_duplicates_are_not_saved(){
        Date now = new Date();
        Date twoDaysAgo = new DateTime().minusDays(2).toDate();
        String userId = TestUtils.randomString();
        String registrationDay1 = "RegistrationDay1";
        String registrationDay2 = "RegistrationDay2";

        Appearance alreadySavedAppearance = getAppearance(registrationDay1, userId, now);

        appearanceLocalService.create(alreadySavedAppearance);

        List<AppearanceDto> newAppearances = new ArrayList<AppearanceDto>();

        AppearanceDto duplicatedAppearance = getAppearanceDto(registrationDay1, userId, now);
        newAppearances.add(duplicatedAppearance);

        AppearanceDto appearanceWithSameTypeButDifferentDate = getAppearanceDto(registrationDay1, userId, twoDaysAgo);
        newAppearances.add(appearanceWithSameTypeButDifferentDate);

        AppearanceDto appearanceWithDifferentTypeButSameDate = getAppearanceDto(registrationDay2, userId, now);
        newAppearances.add(appearanceWithDifferentTypeButSameDate);

        updateLocalService.updateAppearances(newAppearances);

        List<Appearance> firstTypeResult = appearanceLocalService.getAllUserAppearances(userId, registrationDay1);
        List<Appearance> otherTypeResult = appearanceLocalService.getAllUserAppearances(userId, registrationDay2);

        assertEquals(2, firstTypeResult.size());
        assertTrue(firstTypeResult.contains(alreadySavedAppearance));
        assertTrue(firstTypeResult.contains(appearanceFromDto(appearanceWithSameTypeButDifferentDate)));

        assertEquals(1, otherTypeResult.size());
        assertTrue(otherTypeResult.contains(appearanceFromDto(appearanceWithDifferentTypeButSameDate)));
    }

    public void test_if_persons_duplicates_are_not_saved(){
        String firstUserId = "firstUserId";
        String secondUserId = "secondUserId";
        List<PersonDto> persons = new ArrayList<PersonDto>();

        PersonDto firstPerson = new PersonDto();
        firstPerson.setUserId(firstUserId);
        persons.add(firstPerson);

        PersonDto secondPerson = new PersonDto();
        secondPerson.setUserId(secondUserId);
        persons.add(secondPerson);

        PersonDto duplicatedPerson = new PersonDto();
        duplicatedPerson.setUserId(firstUserId);
        persons.add(duplicatedPerson);

        updateLocalService.updatePersons(persons);

        List<Person> result =  personLocalService.findAll();
        assertEquals(2, result.size());

        List<String> userIds = new ArrayList<String>();
        for(Person person : result){
            userIds.add(person.getUserId());
        }

        assertTrue(userIds.contains(firstUserId));
        assertTrue(userIds.contains(secondUserId));

        String newServerUrl = "http://newServerUrl:8080/";
        applicationConfigService.updateServerUrl(newServerUrl);

        // now we can save duplicated person because server url changed, so this person belongs to different server
        // but also only once, the second duplicatedPerson will be ignored
        updateLocalService.updatePersons(Arrays.asList(duplicatedPerson, duplicatedPerson));

        List<Person> resultAfterChangeOfUrl = personLocalService.findAll();

        // returns only users on this server
        assertEquals(1, resultAfterChangeOfUrl.size());
        assertEquals(newServerUrl, resultAfterChangeOfUrl.get(0).getServerUrl());
    }

    public void test_if_arleady_saved_persons_who_were_changed_are_updated(){
        String userId = "userId";
        List<PersonDto> persons = new ArrayList<PersonDto>();

        PersonDto person = new PersonDto();
        person.setUserId(userId);
        person.setFirstName("FirstNameBefore");
        person.setLastName("LastNameBefore");
        persons.add(person);

        updateLocalService.updatePersons(persons);

        List<Person> afterCreationResult =  personLocalService.findAll();
        assertEquals(1, afterCreationResult.size());

        List<PersonDto> changedPersons = new ArrayList<PersonDto>();
        PersonDto changedPerson = new PersonDto();
        changedPerson.setUserId(userId);
        changedPerson.setFirstName("FirstNameAfter");
        changedPerson.setLastName("LastNameAfter");
        changedPersons.add(changedPerson);

        updateLocalService.updatePersons(changedPersons);

        List<Person> afterUpdateResult =  personLocalService.findAll();
        assertEquals(1, afterUpdateResult.size());

        Person afterUpdate = afterUpdateResult.get(0);

        assertEquals(afterUpdate.getUserId(),changedPerson.getUserId());
        assertEquals(afterUpdate.getFirstName(),changedPerson.getFirstName());
        assertEquals(afterUpdate.getLastName(),changedPerson.getLastName());
    }


    private AppearanceDto getAppearanceDto(String name, String userId, Date now) {
        AppearanceDto duplicatedAppearance = new AppearanceDto();
        duplicatedAppearance.setDate(now);
        duplicatedAppearance.setUserId(userId);
        duplicatedAppearance.setAppearanceTypeName(name);
        return duplicatedAppearance;
    }

    private Appearance getAppearance(String name, String userId, Date now) {
        Appearance alreadySavedAppearance = new Appearance();
        alreadySavedAppearance.setDate(now);
        alreadySavedAppearance.setUserId(userId);
        alreadySavedAppearance.setAppearanceTypeName(name);
        return alreadySavedAppearance;
    }

    private Appearance appearanceFromDto(AppearanceDto dto){
        Appearance appearance = new Appearance(dto);
        appearance.setServerUrl(applicationConfigService.getServerUrl());
        return appearance;
    }
}
