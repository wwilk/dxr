package pl.devoxx.dxr.android.appearanceType;

import java.util.List;

import pl.devoxx.dxr.android.core.Callback;

/**
 * Created by wilk on 05/12/15.
 */
public abstract class FindAppearanceTypesCallback implements Callback<List<AppearanceType>>{

    private final AppearanceTypeLocalService appearanceTypeLocalService;

    public FindAppearanceTypesCallback(AppearanceTypeLocalService appearanceTypeLocalService){
        this.appearanceTypeLocalService = appearanceTypeLocalService;
    }

    @Override
    public void onSuccess(List<AppearanceType> result) {
        appearanceTypeLocalService.replaceAll(result);
        onAnyResult("Appearance types refreshed.");
    }

    @Override
    public void onError(Throwable serverError, Throwable localError) {
        onAnyResult("Error during refreshing appearance types. Check server URL and your internet connection." + serverError.getMessage());
    }

    protected abstract void onAnyResult(String message);
}
