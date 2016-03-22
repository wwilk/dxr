package pl.devoxx.dxr.android.registration;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.List;

import pl.devoxx.dxr.android.appearance.Appearance;
import pl.devoxx.dxr.android.core.ServerAwareEntity;
import pl.devoxx.dxr.api.appearance.AppearanceDto;
import pl.devoxx.dxr.api.person.PersonDto;
import pl.devoxx.dxr.api.person.Status;

/**
 * Created by wilk on 22/03/15.
 */
@DatabaseTable(tableName = "person")
public class Person extends ServerAwareEntity {

    @DatabaseField
    private String userId;
    @DatabaseField
    private String email;
    @DatabaseField
    private String firstName;
    @DatabaseField
    private String lastName;
    @DatabaseField
    private Status registrationStatus;

    private List<Appearance> appearances;

    public Person() {
    }

    public Person(PersonDto dto) {
        this.registrationStatus = dto.getRegistrationStatus();
        this.userId = dto.getUserId();
        this.firstName = dto.getFirstName();
        this.lastName = dto.getLastName();
        this.email = dto.getEmail();
        if(dto.getAppearances() != null){
            this.appearances = new ArrayList<Appearance>(dto.getAppearances().size());
            for(AppearanceDto appearanceDto : dto.getAppearances()){
                this.appearances.add(new Appearance(appearanceDto));
            }
        }
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Status getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(Status registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public List<Appearance> getAppearances() {
        return appearances;
    }

    public void setAppearances(List<Appearance> appearances) {
        this.appearances = appearances;
    }

    public PersonDto toDto(){
        PersonDto dto = new PersonDto();
        dto.setFirstName(firstName);
        dto.setUserId(userId);
        dto.setLastName(lastName);
        dto.setEmail(email);
        dto.setRegistrationStatus(registrationStatus);
        if(appearances != null){
            dto.setAppearances(new ArrayList<AppearanceDto>(appearances.size()));
            for(Appearance appearance : appearances){
                dto.getAppearances().add(appearance.toDto());
            }
        }
        return dto;
    }

    @Override
    public String toString(){
        return firstName + " " + lastName + " - " + email;
    }
}