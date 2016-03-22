package pl.devoxx.dxr.android.error;

import pl.devoxx.dxr.android.rest.RestConfig;
import pl.devoxx.dxr.android.rest.RestService;
import pl.devoxx.dxr.api.error.AndroidErrorRemoteService;
import pl.devoxx.dxr.api.error.ErrorInfo;
import pl.devoxx.dxr.api.results.Result;

/**
 * Created by wilk on 02/05/15.
 */
public class DefaultErrorRemoteService implements AndroidErrorRemoteService{

    private final RestService<ErrorInfo> restService;

    public DefaultErrorRemoteService(RestConfig restConfig){
        restService = new RestService<ErrorInfo>(ErrorInfo.class, URL, ErrorInfo[].class, restConfig);
    }

    @Override
    public Result log(ErrorInfo errorInfo) {
        return restService.post(errorInfo);
    }
}
