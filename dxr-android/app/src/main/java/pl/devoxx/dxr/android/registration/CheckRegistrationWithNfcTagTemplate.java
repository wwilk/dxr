package pl.devoxx.dxr.android.registration;

import pl.devoxx.dxr.android.core.Template;
import pl.devoxx.dxr.api.person.PersonDto;
import pl.devoxx.dxr.api.person.PersonRemoteService;

/**
 * Created by wilk on 20/03/15.
 */
public class CheckRegistrationWithNfcTagTemplate extends Template<CheckRegistrationWithNfcTagTemplate.NfcTagRegistrationParams, PersonDto>{

    private final PersonRemoteService personRemoteService;
    private final PersonLocalService personLocalService;

    public CheckRegistrationWithNfcTagTemplate(PersonRemoteService personRemoteService, PersonLocalService personLocalService){
        this.personRemoteService = personRemoteService;
        this.personLocalService = personLocalService;
    }

    @Override
    protected PersonDto callLocalDatabase(NfcTagRegistrationParams params) {
        Person person = personLocalService.findByNfcTagId(params.getNfcTagId(), params.getAppearanceTypeName());
        return person == null ? null : person.toDto();
    }

    @Override
    protected PersonDto callServer(NfcTagRegistrationParams params) {
        return personRemoteService.findByNfcTagId(params.getNfcTagId(), params.getAppearanceTypeName());
    }

    public static class NfcTagRegistrationParams{
        private final String nfcTagId;
        private final String appearanceTypeName;

        public NfcTagRegistrationParams(String nfcTagId, String appearanceTypeName){
            this.nfcTagId = nfcTagId;
            this.appearanceTypeName = appearanceTypeName;
        }

        public String getNfcTagId() {
            return nfcTagId;
        }

        public String getAppearanceTypeName() {
            return appearanceTypeName;
        }
    }
}
