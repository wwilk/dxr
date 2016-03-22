package pl.devoxx.dxr.android.appearance;

import java.util.Date;
import java.util.List;

import pl.devoxx.dxr.android.rest.RestConfig;
import pl.devoxx.dxr.android.rest.RestService;
import pl.devoxx.dxr.api.appearance.AppearanceDto;
import pl.devoxx.dxr.api.appearance.AppearanceRemoteService;
import pl.devoxx.dxr.api.results.Result;

/**
 * Created by wilk on 11/04/15.
 */
public class DefaultAppearanceRemoteService implements AppearanceRemoteService{

    private final RestService<AppearanceDto> restService;

    public DefaultAppearanceRemoteService(RestConfig restConfig){
        restService = new RestService<AppearanceDto>(AppearanceDto.class, URL, AppearanceDto[].class, restConfig);
    }

    @Override
    public Result create(AppearanceDto appearanceDto) {
        return restService.post(appearanceDto);
    }

    @Override
    public List<AppearanceDto> getModified(Date timestamp) {
        return restService.getChangedSince(timestamp);
    }
}
