package pl.devoxx.dxr.android.activity.user_info;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.nfc.NfcAdapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import pl.devoxx.dxr.android.R;
import pl.devoxx.dxr.android.activity.DbActivity;
import pl.devoxx.dxr.android.activity.nfc.NfcConnectionActivity;
import pl.devoxx.dxr.android.appearance.InsertAppearanceCallback;
import pl.devoxx.dxr.android.appearance.InsertAppearanceTaskFactory;
import pl.devoxx.dxr.android.appearance.InsertAppearanceTemplate;
import pl.devoxx.dxr.android.appearanceType.AppearanceType;
import pl.devoxx.dxr.android.core.ServiceFactory;
import pl.devoxx.dxr.android.registration.CheckRegistrationCallback;
import pl.devoxx.dxr.android.registration.CheckRegistrationTemplate;
import pl.devoxx.dxr.android.registration.CheckRegistrationTemplate.RegistrationParams;
import pl.devoxx.dxr.android.registration.CheckRegistrationWithNfcTagTemplate;
import pl.devoxx.dxr.android.registration.CheckRegistrationWithNfcTagTemplate.NfcTagRegistrationParams;
import pl.devoxx.dxr.android.registration.Person;
import pl.devoxx.dxr.android.rest.RestAsyncTask;
import pl.devoxx.dxr.api.appearance.AppearanceDto;
import pl.devoxx.dxr.api.person.PersonDto;


@EActivity(R.layout.user_info_activity)
public class UserInfoActivity extends DbActivity{

    public static final String USER_ID = "userId";
    public static final String NFC_TAG_ID = "nfcTagId";

    private String userId;
    private String nfcTagId;
    private AppearanceType currentlySelectedAppearanceType;
    private UserInfoScreenAdapter userInfoScreenAdapter;
    private ServiceFactory factory;

    @ViewById
    protected Button writeToNfcTagBtn;
    @ViewById
    protected TextView nameLabel;
    @ViewById
    protected TextView surnameLabel;
    @ViewById
    protected TextView emailLabel;
    @ViewById
    protected LinearLayout layout;
    @ViewById
    protected ListView appearancesList;

    @AfterViews
    protected void init(){
        adjustSoundSettings();

        userInfoScreenAdapter = initScreenAdapter();

        userId = getIntent().getStringExtra(USER_ID);
        nfcTagId = getIntent().getStringExtra(NFC_TAG_ID);

        factory = createServiceFactory();

        currentlySelectedAppearanceType = factory.getApplicationConfigService().getConfig().getAppearanceType();

        if(NfcAdapter.getDefaultAdapter(this) == null || !currentlySelectedAppearanceType.isNfcWriteEnabled()){
            userInfoScreenAdapter.hideWriteButton();
        }

        InsertAppearanceCallback insertAppearanceCallback = new InsertAppearanceCallback(userInfoScreenAdapter);
        InsertAppearanceTemplate insertAppearanceTemplate = new InsertAppearanceTemplate(factory.getAppearanceRemoteService(), factory.getAppearanceLocalService());

        if(userId != null) {
            checkRegistrationStatus(insertAppearanceTemplate, insertAppearanceCallback);
        } else{
            checkRegistrationStatusWithNfcTagId(insertAppearanceTemplate, insertAppearanceCallback);
        }
    }

    private UserInfoScreenAdapter initScreenAdapter() {
        ArrayAdapter<AppearanceDto> arrayAdapter = new ArrayAdapter<AppearanceDto>(this, android.R.layout.simple_list_item_1);
        appearancesList.setAdapter(arrayAdapter);
        return new DefaultUserInfoScreenAdapterBuilder()
                .setContext(getApplicationContext())
                .setNameLabel(nameLabel)
                .setSurnameLabel(surnameLabel)
                .setEmailLabel(emailLabel)
                .setArrayAdapter(arrayAdapter)
                .setWriteToNfcButton(writeToNfcTagBtn)
                .setSoundProvider(SoundProvider.instance(getApplicationContext()))
                .setBackground(layout)
                .setStringAccessor(new DefaultUserInfoScreenAdapter.StringAccessor() {
                    @Override
                    public String get(int resId) {
                        return getString(resId);
                    }
                }).createDefaultUserInfoScreenAdapter();
    }

    private void adjustSoundSettings() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 20, 0);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Click(R.id.backBtn)
    public void goBackToHomeActivity(){
        finish();
    }

    @Click(R.id.writeToNfcTagBtn)
    public void readPersonAndGoToNfcConnectionActivity(){
        Person person;
        if(userId != null) {
            person = factory.getPersonLocalService().findByUserId(userId, currentlySelectedAppearanceType.getName());
        } else{
            person = factory.getPersonLocalService().findByNfcTagId(nfcTagId, currentlySelectedAppearanceType.getName());
        }
        if(person == null){
            sendToast(getString(R.string.not_synced_yet));
            return;
        }
        Intent intent = new Intent(this, NfcConnectionActivity.class);
        intent.putExtra(NfcConnectionActivity.USER_ID, person.getUserId());
        intent.putExtra(NfcConnectionActivity.USERNAME, person.toString());
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivityForResult(intent, 200);
    }

    private void checkRegistrationStatus(InsertAppearanceTemplate insertAppearanceTemplate, InsertAppearanceCallback insertAppearanceCallback) {
        CheckRegistrationTemplate checkRegistrationTemplate = new CheckRegistrationTemplate(factory.getPersonRemoteService(), factory.getPersonLocalService());
        InsertAppearanceTaskFactory appearanceTaskFactory = new InsertAppearanceTaskFactory(insertAppearanceCallback,
                insertAppearanceTemplate, currentlySelectedAppearanceType);

        CheckRegistrationCallback callback = new CheckRegistrationCallback(userInfoScreenAdapter, appearanceTaskFactory);
        CheckRegistrationTemplate.RegistrationParams params = new RegistrationParams(userId, currentlySelectedAppearanceType.getName());
        RestAsyncTask<RegistrationParams, PersonDto> task = new RestAsyncTask<RegistrationParams, PersonDto>(checkRegistrationTemplate , params, callback);
        task.execute();
    }

    private void checkRegistrationStatusWithNfcTagId(InsertAppearanceTemplate insertAppearanceTemplate, InsertAppearanceCallback insertAppearanceCallback){
        CheckRegistrationWithNfcTagTemplate checkRegistrationTemplate = new CheckRegistrationWithNfcTagTemplate(
                factory.getPersonRemoteService(), factory.getPersonLocalService());
        InsertAppearanceTaskFactory appearanceTaskFactory = new InsertAppearanceTaskFactory(insertAppearanceCallback,
                insertAppearanceTemplate, currentlySelectedAppearanceType);

        CheckRegistrationCallback callback = new CheckRegistrationCallback(userInfoScreenAdapter, appearanceTaskFactory);
        NfcTagRegistrationParams params = new NfcTagRegistrationParams(nfcTagId, currentlySelectedAppearanceType.getName());
        RestAsyncTask<NfcTagRegistrationParams, PersonDto> task = new RestAsyncTask<NfcTagRegistrationParams, PersonDto>(checkRegistrationTemplate , params, callback);
        task.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        finish(); // go back to HomeActivity
    }

}
