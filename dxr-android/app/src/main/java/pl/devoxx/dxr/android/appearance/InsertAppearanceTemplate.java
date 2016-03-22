package pl.devoxx.dxr.android.appearance;

import pl.devoxx.dxr.android.core.Template;
import pl.devoxx.dxr.api.appearance.AppearanceDto;
import pl.devoxx.dxr.api.appearance.AppearanceRemoteService;
import pl.devoxx.dxr.api.results.Result;

/**
 * Created by wilk on 20/03/15.
 */
public class InsertAppearanceTemplate extends Template<AppearanceDto, Result>{

    private final AppearanceRemoteService appearanceRemoteService;
    private final AppearanceLocalService appearanceLocalService;

    public InsertAppearanceTemplate(AppearanceRemoteService appearanceRemoteService, AppearanceLocalService appearanceLocalService){
        this.appearanceRemoteService = appearanceRemoteService;
        this.appearanceLocalService = appearanceLocalService;
    }

    @Override
    protected Result callLocalDatabase(AppearanceDto argument) {
        Appearance appearance = new Appearance(argument);
        appearance.setPersistedOnServer(false); // this method is called when callServer() failed, so it couldn't be persisted on server
        appearanceLocalService.create(appearance);
        return new Result(true, "saved successfully");
    }

    @Override
    protected Result callServer(AppearanceDto argument) {
        Result result = appearanceRemoteService.create(argument);
        Appearance appearance = new Appearance(argument);
        appearance.setPersistedOnServer(true);
        appearanceLocalService.create(appearance);
        return result;
    }
}
