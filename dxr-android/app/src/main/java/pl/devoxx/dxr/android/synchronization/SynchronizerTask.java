package pl.devoxx.dxr.android.synchronization;

import pl.devoxx.dxr.android.activity.VisibilityAwareActivity;
import pl.devoxx.dxr.android.config.ApplicationConfigService;
import pl.devoxx.dxr.android.config.ImmutableServerUrlProvider;
import pl.devoxx.dxr.android.config.PropertiesHolder;
import pl.devoxx.dxr.android.config.ServerUrlProvider;
import pl.devoxx.dxr.android.core.ServiceFactory;
import pl.devoxx.dxr.android.database.DbHelper;

/**
 * Created by wilk on 21/12/15.
 */
public class SynchronizerTask {

    private final ApplicationConfigService applicationConfigService;
    private final PropertiesHolder propertiesHolder;
    private final DbHelper dbHelper;
    private final VisibilityAwareActivity visibilityAwareActivity;

    public SynchronizerTask(ApplicationConfigService applicationConfigService, PropertiesHolder propertiesHolder, DbHelper dbHelper, VisibilityAwareActivity visibilityAwareActivity) {
        this.applicationConfigService = applicationConfigService;
        this.propertiesHolder = propertiesHolder;
        this.dbHelper = dbHelper;
        this.visibilityAwareActivity = visibilityAwareActivity;
    }

    public void synchronize(){
        String serverUrl = applicationConfigService.getServerUrl();
        ServerUrlProvider serverUrlProvider = new ImmutableServerUrlProvider(serverUrl);
        ServiceFactory factory =  ServiceFactory.withLongTimeoutAndFixedServerUrl(propertiesHolder, dbHelper, serverUrlProvider);
        Synchronizer synchronizer = factory.getSynchronizer(visibilityAwareActivity);
        synchronizer.synchronizeAppearancesAndNfcAutomatically();
    }
}
