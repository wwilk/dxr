package pl.devoxx.dxr.android.activity.home;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Handler;
import android.os.HandlerThread;
import android.widget.Button;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import pl.devoxx.dxr.android.R;
import pl.devoxx.dxr.android.activity.VisibilityAwareActivity;
import pl.devoxx.dxr.android.activity.authentication.AuthenticationActivity_;
import pl.devoxx.dxr.android.activity.info.InfoActivity_;
import pl.devoxx.dxr.android.activity.user_info.UserInfoActivity;
import pl.devoxx.dxr.android.activity.user_info.UserInfoActivity_;
import pl.devoxx.dxr.android.appearanceType.AppearanceType;
import pl.devoxx.dxr.android.config.ApplicationConfig;
import pl.devoxx.dxr.android.config.ApplicationConfigService;
import pl.devoxx.dxr.android.core.ServiceFactory;
import pl.devoxx.dxr.android.nfc_relation.NfcTagAdapter;
import pl.devoxx.dxr.android.report.SponsorReportCallback;
import pl.devoxx.dxr.android.report.SponsorReportTemplate;
import pl.devoxx.dxr.android.rest.RestAsyncTask;
import pl.devoxx.dxr.android.synchronization.SynchronizerTask;
import pl.devoxx.dxr.api.results.Result;
import pl.devoxx.dxr.api.sponsor.SponsorRemoteService;

/**
 * Created by wilk on 09/04/15.
 */
@OptionsMenu(R.menu.menu_main)
@EActivity(R.layout.home_activity)
public class HomeActivity extends VisibilityAwareActivity {

    private NfcTagAdapter nfcTagAdapter;
    private String encryptedUserId;

    private ServiceFactory serviceProvider;

    @ViewById
    protected Button appearanceTypeBtn;
    @ViewById
    protected Button serverUrlBtn;

    @AfterViews
    protected void init(){
        initFields();
        startSynchronization();
    }

    private void initFields() {
        serviceProvider = createServiceFactory();

        ApplicationConfig config = createServiceFactory().getApplicationConfigService().getConfig();
        appearanceTypeBtn.setText(config.getAppearanceType().getName());
        serverUrlBtn.setText(config.getServerUrl());

        nfcTagAdapter = new NfcTagAdapter(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            byte[] rawId = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
            String id = nfcTagAdapter.byteArrayToHexString(rawId);
            goToDisplayActivityWithNfcTagId(id);
        }
    }

    @Click(R.id.scanBtn)
    public void initiateScan(){
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null && scanningResult.getContents() != null) {
            encryptedUserId = scanningResult.getContents();
            goToDisplayActivity();
        } else {
            sendToast("No scan data received!");
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        nfcTagAdapter.disableForegroundDispatch();
    }

    @Override
    public void onResume(){
        super.onResume();
        initFields();
        nfcTagAdapter.enableForegroundDispatch();
    }

    @OptionsItem(R.id.action_settings)
    protected void goToAuthenticationActivity(){
        goTo(AuthenticationActivity_.class);
    }

    @OptionsItem(R.id.action_info)
    protected void goToInfoActivity(){
        goTo(InfoActivity_.class);
    }

    @OptionsItem(R.id.action_send_report)
    protected void sendSponsorReport() {
        ApplicationConfigService configService = serviceProvider.getApplicationConfigService();
        SponsorRemoteService sponsorReportRemoteService = serviceProvider.getSponsorReportRemoteService();
        AppearanceType selectedAppearanceType = configService.getConfig().getAppearanceType();
        SponsorReportTemplate template = new SponsorReportTemplate(sponsorReportRemoteService);
        SponsorReportCallback callback = new SponsorReportCallback(){

            @Override
            protected void sendToast(String text) {
                HomeActivity.this.sendToast(text);
            }

            @Override
            protected String getString(int resId) {
                return HomeActivity.this.getString(resId);
            }
        };
        sendToast(getString(R.string.sponsor_notification));
        new RestAsyncTask<AppearanceType, Result>(template, selectedAppearanceType, callback).execute();
    }

    private void goToDisplayActivity(){
        Intent intent = new Intent(this, UserInfoActivity_.class);
        intent.putExtra(UserInfoActivity.USER_ID, encryptedUserId);
        intent.putExtra(UserInfoActivity.NFC_TAG_ID, (String)null);
        startActivity(intent);
    }

    private void goToDisplayActivityWithNfcTagId(String nfcTagId){
        Intent intent = new Intent(this, UserInfoActivity_.class);
        intent.putExtra(UserInfoActivity.NFC_TAG_ID, nfcTagId);
        intent.putExtra(UserInfoActivity.USER_ID, (String) null);
        startActivity(intent);
    }

    private void startSynchronization() {
        final SynchronizerTask task = new SynchronizerTask(serviceProvider.getApplicationConfigService(),
                properties(), getHelper(), this);
        HandlerThread thread = new HandlerThread("SynchronizerThread");
        thread.start();
        final Handler handler = new Handler(thread.getLooper());
        final long period = properties().getSynchronizationPeriod();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                task.synchronize();
                handler.postDelayed(this, period);
            }
        };
        handler.postDelayed(runnable, period);
    }

}
