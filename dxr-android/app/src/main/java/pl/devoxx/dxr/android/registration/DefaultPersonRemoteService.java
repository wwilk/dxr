package pl.devoxx.dxr.android.registration;

import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

import pl.devoxx.dxr.android.rest.RestConfig;
import pl.devoxx.dxr.android.rest.RestService;
import pl.devoxx.dxr.api.person.PersonDto;
import pl.devoxx.dxr.api.person.PersonRemoteService;

/**
 * Created by wilk on 12/04/15.
 */
public class DefaultPersonRemoteService implements PersonRemoteService{

    private final RestService<PersonDto> restService;

    public DefaultPersonRemoteService(RestConfig restConfig){
        restService = new RestService<PersonDto>(PersonDto.class, URL, PersonDto[].class, restConfig);
    }

    @Override
    public PersonDto findByNfcTagId(final String nfcTagId, String appearanceTypeName){
        return restService.custom(new RestService.CustomAction<PersonDto>() {
            @Override
            public PersonDto execute(String baseUrl, RestTemplate template) {
                String url = baseUrl + "/nfc/" + nfcTagId;
                return template.getForObject(url, PersonDto.class);
            }
        });
    }

    @Override
    public PersonDto findById(String userId, String appearanceTypeName) {
        String query = userId + "?appearanceTypeName" + appearanceTypeName;
        return restService.get(query);
    }

    @Override
    public List<PersonDto> getModified(Date timestamp) {
        return restService.getChangedSince(timestamp);
    }
}
