package pl.devoxx.dxr.android.appearanceType;

import java.util.List;

import pl.devoxx.dxr.android.rest.RestConfig;
import pl.devoxx.dxr.android.rest.RestService;
import pl.devoxx.dxr.api.appearance_type.AppearanceTypeDto;
import pl.devoxx.dxr.api.appearance_type.AppearanceTypeRemoteService;

/**
 * Created by wilk on 05/12/15.
 */
public class DefaultAppearanceTypeRemoteService implements AppearanceTypeRemoteService{

    private final RestService<AppearanceTypeDto> restService;

    public DefaultAppearanceTypeRemoteService(RestConfig restConfig){
        restService = new RestService<AppearanceTypeDto>(AppearanceTypeDto.class, URL, AppearanceTypeDto[].class, restConfig);
    }

    @Override
    public List<AppearanceTypeDto> getAllEnabledAppearanceTypes() {
        return restService.getList("");
    }
}
