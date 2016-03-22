package pl.devoxx.dxr.android.registration;

import pl.devoxx.dxr.android.core.Template;
import pl.devoxx.dxr.api.person.PersonDto;
import pl.devoxx.dxr.api.person.PersonRemoteService;

/**
 * Created by wilk on 20/03/15.
 */
public class CheckRegistrationTemplate extends Template<CheckRegistrationTemplate.RegistrationParams, PersonDto>{

    private final PersonRemoteService personRemoteService;
    private final PersonLocalService personLocalService;

    public CheckRegistrationTemplate(PersonRemoteService personRemoteService, PersonLocalService personLocalService){
        this.personRemoteService = personRemoteService;
        this.personLocalService = personLocalService;
    }

    @Override
    protected PersonDto callLocalDatabase(RegistrationParams params) {
        Person person = personLocalService.findByUserId(params.getUserId(), params.getAppearanceTypeName());
        return person == null ? null : person.toDto();
    }

    @Override
    protected PersonDto callServer(RegistrationParams params) {
        return personRemoteService.findById(params.getUserId(), params.getAppearanceTypeName());
    }

    public static class RegistrationParams{
        private final String userId;
        private final String appearanceTypeName;

        public RegistrationParams(String userId, String appearanceTypeName){
            this.userId = userId;
            this.appearanceTypeName = appearanceTypeName;
        }

        public String getUserId() {
            return userId;
        }

        public String getAppearanceTypeName() {
            return appearanceTypeName;
        }
    }
}
