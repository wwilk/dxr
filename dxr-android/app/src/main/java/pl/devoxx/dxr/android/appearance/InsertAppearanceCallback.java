package pl.devoxx.dxr.android.appearance;

import pl.devoxx.dxr.android.R;
import pl.devoxx.dxr.android.activity.user_info.UserInfoScreenAdapter;
import pl.devoxx.dxr.android.core.Callback;
import pl.devoxx.dxr.api.results.Result;

/**
 * Created by wilk on 21/03/15.
 */
public class InsertAppearanceCallback implements Callback<Result> {

    private final UserInfoScreenAdapter userInfoScreenAdapter;

    public InsertAppearanceCallback(UserInfoScreenAdapter userInfoScreenAdapter){
        this.userInfoScreenAdapter = userInfoScreenAdapter;
    }

    @Override
    public void onSuccess(Result result) {
        userInfoScreenAdapter.sendToast(userInfoScreenAdapter.getMessage(R.string.appearance_save_success));
    }
    @Override
    public void onError(Throwable serverError, Throwable localError) {
        userInfoScreenAdapter.sendToast(userInfoScreenAdapter.getMessage(R.string.appearance_save_error));
    }
}
