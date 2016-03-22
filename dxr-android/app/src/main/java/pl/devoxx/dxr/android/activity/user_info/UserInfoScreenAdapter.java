package pl.devoxx.dxr.android.activity.user_info;

import java.util.List;

import pl.devoxx.dxr.api.appearance.AppearanceDto;

/**
 * Created by wilk on 14/03/15.
 */
public interface UserInfoScreenAdapter {
    void displayName(String name);
    void displaySurname(String surname);
    void displayEmail(String email);
    void fillRed();
    void fillYellow();
    void fillGreen();
    void refreshAppearances(List<AppearanceDto> appearances);
    void sendToast(String msg);
    void playSuccessSound();
    void playFailureSound();
    void hideWriteButton();
    String getMessage(int resId);
}
