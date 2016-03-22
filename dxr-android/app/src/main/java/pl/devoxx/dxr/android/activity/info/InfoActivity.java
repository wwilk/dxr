package pl.devoxx.dxr.android.activity.info;

import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.Date;

import pl.devoxx.dxr.android.R;
import pl.devoxx.dxr.android.activity.DbActivity;
import pl.devoxx.dxr.android.core.ServiceFactory;
import pl.devoxx.dxr.android.synchronization.person.PersonSynchronization;

/**
 * Created by wilk on 06/12/15.
 */
@EActivity(R.layout.info_activity)
public class InfoActivity extends DbActivity {

    @ViewById
    protected TextView lastSyncText;
    @ViewById
    protected TextView lastParticipantSyncText;
    @ViewById
    protected TextView participantsCountText;
    @ViewById
    protected TextView appearancesCountText;
    @ViewById
    protected TextView nfcConnectionsCountText;
    @ViewById
    protected TextView lastParticipantSyncServerUrlText;
    @ViewById
    protected TextView lastParticipantSyncPeopleCountText;

    @AfterViews
    protected void initFields(){
        ServiceFactory serviceProvider = createServiceFactory();

        appearancesCountText.setText(String.valueOf(serviceProvider.getAppearanceLocalService().countAll()));
        nfcConnectionsCountText.setText(String.valueOf(serviceProvider.getNfcRelationLocalService().countAll()));
        participantsCountText.setText(String.valueOf(serviceProvider.getPersonLocalService().countAll()));

        Date lastSyncDate = serviceProvider.getSynchronizationService().getLastSynchronizationDate();
        if(lastSyncDate != null){
            lastSyncText.setText(lastSyncDate.toString());
        } else{
            lastSyncText.setText("NEVER");
        }

        PersonSynchronization lastPersonSync = serviceProvider.getPersonSynchronizationService().getLastSynchronization();

        if(lastPersonSync != null){
            lastParticipantSyncText.setText(lastPersonSync.getDate().toString());
            lastParticipantSyncServerUrlText.setText(lastPersonSync.getServerUrl());
            lastParticipantSyncPeopleCountText.setText(String.valueOf(lastPersonSync.getSynchronizedPeopleCount()));
        } else{
            lastParticipantSyncText.setText("NEVER");
            lastParticipantSyncServerUrlText.setText("n/a");
            lastParticipantSyncPeopleCountText.setText("n/a");
        }
    }
}
