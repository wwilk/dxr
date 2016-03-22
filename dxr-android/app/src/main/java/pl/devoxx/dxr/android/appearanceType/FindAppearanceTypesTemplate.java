package pl.devoxx.dxr.android.appearanceType;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import pl.devoxx.dxr.android.core.Template;
import pl.devoxx.dxr.android.rest.AsyncTaskResult;
import pl.devoxx.dxr.api.appearance_type.AppearanceTypeDto;
import pl.devoxx.dxr.api.appearance_type.AppearanceTypeRemoteService;

/**
 * Created by wilk on 05/12/15.
 */
public class FindAppearanceTypesTemplate extends Template<Void, List<AppearanceType>>{

    private final AppearanceTypeRemoteService appearanceTypeRemoteService;

    public FindAppearanceTypesTemplate(AppearanceTypeRemoteService appearanceTypeRemoteService) {
        this.appearanceTypeRemoteService = appearanceTypeRemoteService;
    }

    @Override
    protected List<AppearanceType> callLocalDatabase(Void argument) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected List<AppearanceType> callServer(Void argument) {
        List<AppearanceType> result = new ArrayList<AppearanceType>();
        for(AppearanceTypeDto dto : appearanceTypeRemoteService.getAllEnabledAppearanceTypes()){
            result.add(new AppearanceType(dto));
        }
        return result;
    }

    public AsyncTaskResult<List<AppearanceType>> call(Void argument){
        try{
            return new AsyncTaskResult<List<AppearanceType>>(callServer(argument));
        } catch(Exception serverException){
            Log.e(this.getClass().getSimpleName(), "execute to server failed", serverException);
            return new AsyncTaskResult<List<AppearanceType>>(serverException, null);
        }
    }

}
