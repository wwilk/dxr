package pl.devoxx.dxr.android.nfc_relation;

import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

import pl.devoxx.dxr.android.rest.RestConfig;
import pl.devoxx.dxr.android.rest.RestService;
import pl.devoxx.dxr.api.nfc_relation.NfcRelationDto;
import pl.devoxx.dxr.api.nfc_relation.NfcRelationRemoteService;
import pl.devoxx.dxr.api.results.CreateRelationResult;

/**
 * Created by wilk on 12/06/15.
 */
public class DefaultNfcRelationRemoteService implements NfcRelationRemoteService {

    private final RestService<NfcRelationDto> nfcRelationRestService;

    public DefaultNfcRelationRemoteService(RestConfig restConfig){
        this.nfcRelationRestService = new RestService<NfcRelationDto>(
                NfcRelationDto.class, URL, NfcRelationDto[].class, restConfig);
    }

    public CreateRelationResult create(final NfcRelationDto arg){
        return nfcRelationRestService.custom(new RestService.CustomAction<CreateRelationResult>() {
            @Override
            public CreateRelationResult execute(String baseUrl, RestTemplate template) {
                return template.postForObject(baseUrl, arg, CreateRelationResult.class);
            }
        });
    }

    @Override
    public List<NfcRelationDto> getModified(Date timestamp) {
        return nfcRelationRestService.getChangedSince(timestamp);
    }
}
