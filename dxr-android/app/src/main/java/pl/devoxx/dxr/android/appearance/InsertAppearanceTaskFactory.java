package pl.devoxx.dxr.android.appearance;

import java.util.Date;

import pl.devoxx.dxr.android.appearanceType.AppearanceType;
import pl.devoxx.dxr.android.rest.RestAsyncTask;
import pl.devoxx.dxr.api.appearance.AppearanceDto;
import pl.devoxx.dxr.api.results.Result;

/**
 * Created by wilk on 12/06/15.
 */
public class InsertAppearanceTaskFactory {

    private final InsertAppearanceCallback callback;
    private final InsertAppearanceTemplate template;
    private final AppearanceType selectedType;

    public InsertAppearanceTaskFactory(InsertAppearanceCallback callback, InsertAppearanceTemplate template, AppearanceType selectedType) {
        this.callback = callback;
        this.template = template;
        this.selectedType = selectedType;
    }

    public RestAsyncTask<AppearanceDto, Result> create(String userId){
        AppearanceDto appearanceDto = new AppearanceDto();
        appearanceDto.setUserId(userId);
        appearanceDto.setDate(new Date());
        appearanceDto.setAppearanceTypeName(selectedType.getName());
        return new RestAsyncTask<AppearanceDto, Result>(template, appearanceDto, callback);
    }
}
