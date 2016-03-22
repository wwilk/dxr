package pl.devoxx.dxr.android.activity.settings;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import pl.devoxx.dxr.android.R;
import pl.devoxx.dxr.android.activity.VisibilityAwareActivity;
import pl.devoxx.dxr.android.appearanceType.AppearanceType;
import pl.devoxx.dxr.android.appearanceType.AppearanceTypeLocalService;
import pl.devoxx.dxr.android.appearanceType.FindAppearanceTypesCallback;
import pl.devoxx.dxr.android.appearanceType.FindAppearanceTypesTemplate;
import pl.devoxx.dxr.android.config.ApplicationConfig;
import pl.devoxx.dxr.android.config.ApplicationConfigService;
import pl.devoxx.dxr.android.core.ServiceFactory;
import pl.devoxx.dxr.android.rest.RestAsyncTask;
import pl.devoxx.dxr.android.synchronization.ManualSynchronizationAsyncTask;
import pl.devoxx.dxr.android.synchronization.Synchronizer;
import pl.devoxx.dxr.api.appearance_type.AppearanceTypeRemoteService;

/**
 * Created by wilk on 09/08/15.
 */
@EActivity(R.layout.settings_activity)
public class SettingsActivity extends VisibilityAwareActivity{

    private ApplicationConfigService configService;
    private AppearanceTypeLocalService appearanceTypeLocalService;
    private AppearanceTypeRemoteService appearanceTypeRemoteService;

    private AppearanceTypeDropdown appearanceTypeDropdown;

    @ViewById
    protected ProgressBar sendingBar;
    @ViewById
    protected EditText serverUrlText;

    @AfterViews
    protected void init(){
        ServiceFactory serviceProvider = createServiceFactory();
        configService = serviceProvider.getApplicationConfigService();
        appearanceTypeLocalService = serviceProvider.getAppearanceTypeLocalService();
        appearanceTypeRemoteService = serviceProvider.getAppearanceTypeRemoteService();

        hideProgressBar();

        ApplicationConfig config = configService.getConfig();

        serverUrlText.setText(config.getServerUrl());

        initAppearanceTypeDropdown();
    }

    private void initAppearanceTypeDropdown(){
        ServiceFactory serviceProvider = createServiceFactory();
        List<AppearanceType> availableTypes = serviceProvider.getAppearanceTypeLocalService().findAll();
        ApplicationConfig config = serviceProvider.getApplicationConfigService().getConfig();
        AppearanceType currentlySelectedAppearanceType = config.getAppearanceType();
        appearanceTypeDropdown = new AppearanceTypeDropdown(this,
                new AppearanceTypeDropdown.SelectCallback() {
                    @Override
                    public void onSelect() {
                        configService.updateAppearanceType(appearanceTypeDropdown.getSelected());
                        sendToast(getString(R.string.appearance_type_changed));
                    }
                },
                (Button) findViewById(R.id.appearanceTypes),
                currentlySelectedAppearanceType,
                availableTypes);
    }

    @Click(R.id.saveUrlBtn)
    public void saveServerUrl(){
        String newUrl = serverUrlText.getText().toString();
        if(!newUrl.endsWith("/")){
            newUrl = newUrl.substring(0, newUrl.length() - 1);
        }

        configService.updateServerUrl(newUrl);

        showProgressBar();
        sendToast(getString(R.string.sync_in_progress));
        FindAppearanceTypesCallback callback = new FindAppearanceTypesCallback(appearanceTypeLocalService) {
            @Override
            protected void onAnyResult(String message) {
                SettingsActivity.this.sendToast(message);
                hideProgressBar();
                configService.setAppearanceTypeToDefault();
                initAppearanceTypeDropdown();
            }
        };
        FindAppearanceTypesTemplate template = new FindAppearanceTypesTemplate(appearanceTypeRemoteService);
        new RestAsyncTask<Void, List<AppearanceType>>(template, null, callback).execute();
    }

    @Click(R.id.updateParticipantsBtn)
    public void synchronizeParticipants(){
        final Synchronizer synchronizer = createServiceFactory().getSynchronizer(this);
        new ManualSynchronizationAsyncTask(synchronizer){
            @Override
            public void sendToast(String message){
                SettingsActivity.this.sendToast(message);
            }
        }.execute();
    }

    private void showProgressBar(){
        sendingBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar(){
        sendingBar.setVisibility(View.GONE);
    }

}
