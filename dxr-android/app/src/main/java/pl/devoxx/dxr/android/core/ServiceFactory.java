package pl.devoxx.dxr.android.core;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import java.nio.charset.Charset;

import pl.devoxx.dxr.android.activity.VisibilityAwareActivity;
import pl.devoxx.dxr.android.appearance.AppearanceLocalService;
import pl.devoxx.dxr.android.appearance.DefaultAppearanceRemoteService;
import pl.devoxx.dxr.android.appearanceType.AppearanceTypeLocalService;
import pl.devoxx.dxr.android.appearanceType.DefaultAppearanceTypeRemoteService;
import pl.devoxx.dxr.android.config.ApplicationConfigService;
import pl.devoxx.dxr.android.config.PropertiesHolder;
import pl.devoxx.dxr.android.config.ServerUrlProvider;
import pl.devoxx.dxr.android.database.DbHelper;
import pl.devoxx.dxr.android.error.DefaultErrorRemoteService;
import pl.devoxx.dxr.android.nfc_relation.DefaultNfcRelationRemoteService;
import pl.devoxx.dxr.android.nfc_relation.NfcRelationLocalService;
import pl.devoxx.dxr.android.registration.DefaultPersonRemoteService;
import pl.devoxx.dxr.android.registration.PersonLocalService;
import pl.devoxx.dxr.android.report.DefaultSponsorRemoteService;
import pl.devoxx.dxr.android.rest.DefaultRestConfig;
import pl.devoxx.dxr.android.rest.RestConfig;
import pl.devoxx.dxr.android.synchronization.SynchronizationService;
import pl.devoxx.dxr.android.synchronization.Synchronizer;
import pl.devoxx.dxr.android.synchronization.UpdateLocalService;
import pl.devoxx.dxr.android.synchronization.UpdateRemoteService;
import pl.devoxx.dxr.android.synchronization.person.PersonSynchronizationService;
import pl.devoxx.dxr.api.appearance.AppearanceRemoteService;
import pl.devoxx.dxr.api.appearance_type.AppearanceTypeRemoteService;
import pl.devoxx.dxr.api.encryption.EncryptionService;
import pl.devoxx.dxr.api.error.AndroidErrorRemoteService;
import pl.devoxx.dxr.api.nfc_relation.NfcRelationRemoteService;
import pl.devoxx.dxr.api.person.PersonRemoteService;
import pl.devoxx.dxr.api.sponsor.SponsorRemoteService;

/**
 * Created by wilk on 12/04/15.
 */
public class ServiceFactory {

    private final PropertiesHolder propertiesHolder;
    private final DbHelper dbHelper;
    private final RestConfig restConfig;
    private final ServerUrlProvider serverUrlProvider;

    private ServiceFactory(PropertiesHolder propertiesHolder, DbHelper dbHelper, ServerUrlProvider serverUrlProvider,
                           RestConfig restConfig){
        this.propertiesHolder = propertiesHolder;
        this.dbHelper = dbHelper;
        this.serverUrlProvider = serverUrlProvider;
        this.restConfig = restConfig;
    }

    public static ServiceFactory withShortTimeoutAndDynamicServerUrl(PropertiesHolder propertiesHolder, DbHelper dbHelper){
        ServerUrlProvider serverUrlProvider = new ApplicationConfigService(dbHelper.getApplicationConfigDao(), dbHelper.getAppearanceTypeDao());
        RestConfig restConfig = new DefaultRestConfig(propertiesHolder.getReadTimeout(), propertiesHolder.getConnectTimeout(), serverUrlProvider);
        return new ServiceFactory(propertiesHolder, dbHelper, serverUrlProvider, restConfig);
    }

    public static ServiceFactory withLongTimeoutAndFixedServerUrl(PropertiesHolder propertiesHolder, DbHelper dbHelper, ServerUrlProvider serverUrlProvider){
        RestConfig restConfig = new DefaultRestConfig(propertiesHolder.getIncreasedReadTimeout(), propertiesHolder.getIncreasedConnectTimeout(), serverUrlProvider);
        return new ServiceFactory(propertiesHolder, dbHelper, serverUrlProvider, restConfig);
    }

    public PersonRemoteService getPersonRemoteService() {
        return new DefaultPersonRemoteService(restConfig);
    }

    public AppearanceRemoteService getAppearanceRemoteService() {
        return new DefaultAppearanceRemoteService(restConfig);
    }

    public AppearanceLocalService getAppearanceLocalService() {
        return new AppearanceLocalService(serverUrlProvider, dbHelper.getAppearanceDao());
    }

    public PersonLocalService getPersonLocalService() {
        return new PersonLocalService(dbHelper.getPersonDao(), getAppearanceLocalService(), getNfcRelationLocalService(), serverUrlProvider);
    }

    public ApplicationConfigService getApplicationConfigService(){ return new ApplicationConfigService(dbHelper.getApplicationConfigDao(), dbHelper.getAppearanceTypeDao()); }

    public NfcRelationLocalService getNfcRelationLocalService(){
        return new NfcRelationLocalService(serverUrlProvider, dbHelper.getNfcRelationDao());
    }

    public NfcRelationRemoteService getNfcRelationRemoteService(){
        return new DefaultNfcRelationRemoteService(restConfig);
    }

    public SponsorRemoteService getSponsorReportRemoteService(){
        return new DefaultSponsorRemoteService(restConfig);
    }

    public EncryptionService getEncryptionService(){
        return new EncryptionService(getSecurityKeyHash().toString());
    }

    public AppearanceTypeRemoteService getAppearanceTypeRemoteService() {
        return new DefaultAppearanceTypeRemoteService(restConfig);
    }

    public AppearanceTypeLocalService getAppearanceTypeLocalService() {
        return new AppearanceTypeLocalService(serverUrlProvider, dbHelper.getAppearanceTypeDao());
    }

    public PersonSynchronizationService getPersonSynchronizationService(){
        return new PersonSynchronizationService(dbHelper.getPersonSynchronizationDao(), serverUrlProvider);
    }

    public SynchronizationService getSynchronizationService() {
        return new SynchronizationService(dbHelper.getSynchronizationDao(), serverUrlProvider);
    }

    public AndroidErrorRemoteService getErrorRemoteService(){
        return new DefaultErrorRemoteService(restConfig);
    }

    public UpdateLocalService getUpdateLocalService(){
        return new UpdateLocalService(getAppearanceLocalService(),getPersonLocalService(), getNfcRelationLocalService());
    }

    public UpdateRemoteService getUpdateRemoteService(){
        return new UpdateRemoteService(getAppearanceRemoteService(), getAppearanceLocalService(),
                getNfcRelationRemoteService(), getNfcRelationLocalService());
    }

    public Synchronizer getSynchronizer(VisibilityAwareActivity context) {
        return new Synchronizer(getUpdateLocalService(), getSynchronizationService(), getUpdateRemoteService(), getAppearanceRemoteService(),
                getPersonRemoteService(), context, getNfcRelationRemoteService(), getEncryptionService(), getPersonSynchronizationService());
    }

    private HashCode getSecurityKeyHash(){
        return Hashing.sha1().hashString(propertiesHolder.getSecurityKey(), Charset.forName("UTF-8"));
    }
}
