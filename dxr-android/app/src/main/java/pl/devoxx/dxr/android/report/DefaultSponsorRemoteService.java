package pl.devoxx.dxr.android.report;

import org.springframework.web.client.RestTemplate;

import pl.devoxx.dxr.android.rest.RestConfig;
import pl.devoxx.dxr.android.rest.RestService;
import pl.devoxx.dxr.api.results.Result;
import pl.devoxx.dxr.api.sponsor.SponsorRemoteService;

/**
 * Created by wilk on 09/08/15.
 */
public class DefaultSponsorRemoteService implements SponsorRemoteService {

    private static final String SEND = "/send-report/";
    private final RestService<Result> restService;

    public DefaultSponsorRemoteService(RestConfig restConfig){
        restService = new RestService<Result>(Result.class, URL, Result[].class, restConfig);
    }

    @Override
    public Result sendReport(final String appearanceTypeName){
        return restService.custom(new RestService.CustomAction<Result>() {
            @Override
            public Result execute(String baseUrl, RestTemplate template) {
                String url = baseUrl + appearanceTypeName + SEND;
                return template.postForObject(url, null, Result.class);
            }
        });
    }
}
