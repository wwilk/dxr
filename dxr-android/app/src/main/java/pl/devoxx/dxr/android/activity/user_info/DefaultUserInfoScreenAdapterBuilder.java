package pl.devoxx.dxr.android.activity.user_info;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import pl.devoxx.dxr.api.appearance.AppearanceDto;

public class DefaultUserInfoScreenAdapterBuilder {
    private Context context;
    private TextView nameLabel;
    private TextView surnameLabel;
    private TextView emailLabel;
    private ArrayAdapter<AppearanceDto> arrayAdapter;
    private LinearLayout background;
    private DefaultUserInfoScreenAdapter.StringAccessor stringAccessor;
    private Button writeToNfcButton;
    private SoundProvider soundProvider;

    public DefaultUserInfoScreenAdapterBuilder setContext(Context context) {
        this.context = context;
        return this;
    }

    public DefaultUserInfoScreenAdapterBuilder setNameLabel(TextView nameLabel) {
        this.nameLabel = nameLabel;
        return this;
    }

    public DefaultUserInfoScreenAdapterBuilder setSurnameLabel(TextView surnameLabel) {
        this.surnameLabel = surnameLabel;
        return this;
    }

    public DefaultUserInfoScreenAdapterBuilder setEmailLabel(TextView emailLabel) {
        this.emailLabel = emailLabel;
        return this;
    }

    public DefaultUserInfoScreenAdapterBuilder setArrayAdapter(ArrayAdapter<AppearanceDto> arrayAdapter) {
        this.arrayAdapter = arrayAdapter;
        return this;
    }

    public DefaultUserInfoScreenAdapterBuilder setBackground(LinearLayout background) {
        this.background = background;
        return this;
    }

    public DefaultUserInfoScreenAdapterBuilder setStringAccessor(DefaultUserInfoScreenAdapter.StringAccessor stringAccessor) {
        this.stringAccessor = stringAccessor;
        return this;
    }

    public DefaultUserInfoScreenAdapterBuilder setWriteToNfcButton(Button writeToNfcButton) {
        this.writeToNfcButton = writeToNfcButton;
        return this;
    }

    public DefaultUserInfoScreenAdapterBuilder setSoundProvider(SoundProvider soundProvider) {
        this.soundProvider = soundProvider;
        return this;
    }

    public DefaultUserInfoScreenAdapter createDefaultUserInfoScreenAdapter() {
        return new DefaultUserInfoScreenAdapter(context, nameLabel, surnameLabel, emailLabel, arrayAdapter, background, stringAccessor, writeToNfcButton, soundProvider);
    }
}