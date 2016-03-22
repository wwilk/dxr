package pl.devoxx.dxr.android.activity.user_info;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import pl.devoxx.dxr.api.appearance.AppearanceDto;

/**
 * Created by wilk on 15/03/15.
 */
class DefaultUserInfoScreenAdapter implements UserInfoScreenAdapter {

    private final TextView nameLabel;
    private final TextView surnameLabel;
    private final TextView emailLabel;
    private final ArrayAdapter<AppearanceDto> arrayAdapter;
    private final Context context;
    private final LinearLayout background;
    private final SoundProvider soundProvider;
    private final StringAccessor stringAccessor;
    private final Button writeToNfcButton;

    DefaultUserInfoScreenAdapter(Context context, TextView nameLabel, TextView surnameLabel, TextView emailLabel,
                                        ArrayAdapter<AppearanceDto> arrayAdapter, LinearLayout background,
                                        StringAccessor stringAccessor, Button writeToNfcButton, SoundProvider soundProvider){
        this.context = context;
        this.nameLabel = nameLabel;
        this.surnameLabel = surnameLabel;
        this.emailLabel = emailLabel;
        this.arrayAdapter = arrayAdapter;
        this.background = background;
        this.stringAccessor = stringAccessor;
        this.writeToNfcButton = writeToNfcButton;
        this.soundProvider = soundProvider;
    }

    public void displayName(String name){
        nameLabel.setText(name);
    }

    public void displaySurname(String surname){
        surnameLabel.setText(surname);
    }

    public void displayEmail(String email){
        emailLabel.setText(email);
    }

    public void fillRed(){
        background.setBackgroundColor(Color.RED);
    }

    public void fillYellow(){
        background.setBackgroundColor(Color.YELLOW);
    }

    public void fillGreen(){
        background.setBackgroundColor(Color.GREEN);
    }

    @Override
    public void playSuccessSound() {
        soundProvider.playSuccessSound();
    }

    @Override
    public void playFailureSound() {
        soundProvider.playFailureSound();
    }

    public void refreshAppearances(List<AppearanceDto> appearances){
        if(appearances != null) {
            arrayAdapter.clear();
            arrayAdapter.addAll(appearances);
        }
    }

    public void sendToast(String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void hideWriteButton() {
        writeToNfcButton.setVisibility(View.GONE);
    }

    public String getMessage(int resId){
        return stringAccessor.get(resId);
    }

    public interface StringAccessor{
        String get(int resId);
    }
}
