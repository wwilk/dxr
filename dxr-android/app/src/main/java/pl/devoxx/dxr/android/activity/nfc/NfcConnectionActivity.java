package pl.devoxx.dxr.android.activity.nfc;

import android.content.Intent;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import pl.devoxx.dxr.android.R;
import pl.devoxx.dxr.android.activity.DbActivity;
import pl.devoxx.dxr.android.core.ServiceFactory;
import pl.devoxx.dxr.android.nfc_relation.ConnectNfcTagCallback;
import pl.devoxx.dxr.android.nfc_relation.ConnectNfcTagTemplate;
import pl.devoxx.dxr.android.nfc_relation.NfcRelationLocalService;
import pl.devoxx.dxr.android.nfc_relation.NfcTagAdapter;
import pl.devoxx.dxr.android.rest.RestAsyncTask;
import pl.devoxx.dxr.api.nfc_relation.NfcRelationDto;
import pl.devoxx.dxr.api.nfc_relation.NfcRelationRemoteService;
import pl.devoxx.dxr.api.results.CreateRelationResult;

/**
 * Created by wilk on 09/04/15.
 */
@EActivity(R.layout.nfc_connection_activity)
public class NfcConnectionActivity extends DbActivity{

    public static final String USER_ID = "userId";
    public static final String USERNAME = "username";

    private String userId;

    @ViewById
    protected LinearLayout backgroundLayout;
    @ViewById
    protected ProgressBar loadingBar;
    @ViewById
    protected TextView usernameTxt;

    private NfcTagAdapter adapter;

    private NfcRelationRemoteService nfcRelationRemoteService;
    private NfcRelationLocalService nfcRelationLocalService;

    @AfterViews
    protected void init(){
        userId = getIntent().getStringExtra(USER_ID);
        String username = getIntent().getStringExtra(USERNAME);

        usernameTxt.setText(username);
        loadingBar.setVisibility(View.VISIBLE);

        adapter = new NfcTagAdapter(this);

        ServiceFactory factory = createServiceFactory();
        nfcRelationRemoteService = factory.getNfcRelationRemoteService();
        nfcRelationLocalService = factory.getNfcRelationLocalService();
    }

    @Click(R.id.backBtn)
    public void goBackToHomeActivity() {
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent){
        loadingBar.setVisibility(View.GONE);
        backgroundLayout.setBackgroundColor(Color.LTGRAY);
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            byte[] rawId = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
            String nfcTagId = adapter.byteArrayToHexString(rawId);
            connectUserAndNfcTag(userId, nfcTagId);
        }
    }

    private void connectUserAndNfcTag(String userId, String nfcTagId){
        final NfcRelationDto dto = new NfcRelationDto(userId, nfcTagId);

        ConnectNfcTagCallback callback = new ConnectNfcTagCallback(nfcRelationLocalService, dto) {
            @Override
            protected void onSuccess() {
                adapter.cancelPendingIntent();
                backgroundLayout.setBackgroundColor(Color.GREEN);
                finish();
                sendToast(getString(R.string.relation_created));
            }

            @Override
            protected void onErrorResult(CreateRelationResult result) {
                handleErrorResult(result);
            }

            @Override
            protected void onExceptionThrown(Throwable serverError) {
                handleError(serverError.getMessage());
            }
        };
        ConnectNfcTagTemplate template = new ConnectNfcTagTemplate(nfcRelationRemoteService, nfcRelationLocalService);

        new RestAsyncTask<NfcRelationDto, CreateRelationResult>(template, dto, callback).execute();
    }

    private void handleErrorResult(CreateRelationResult result){
        if(result == CreateRelationResult.RELATION_ALREADY_EXISTS){
            handleError(getString(R.string.relation_already_exists_error));
        } else if(result == CreateRelationResult.TAG_ALREADY_HAS_ANOTHER_RELATION){
            handleError(getString(R.string.relation_for_tag_already_exists_error));
        }
    }

    private void handleError(String message){
        loadingBar.setVisibility(View.VISIBLE);
        backgroundLayout.setBackgroundColor(Color.RED);
        sendToast(message);
    }

    @Override
    public void onPause(){
        super.onPause();
        adapter.disableForegroundDispatch();
    }

    @Override
    public void onResume(){
        super.onResume();
        adapter.enableForegroundDispatch();
    }

}
