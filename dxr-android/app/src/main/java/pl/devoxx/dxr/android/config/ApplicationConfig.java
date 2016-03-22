package pl.devoxx.dxr.android.config;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import pl.devoxx.dxr.android.appearanceType.AppearanceType;
import pl.devoxx.dxr.android.core.ServerAwareEntity;


/**
 * Created by wilk on 14/04/15.
 */
@DatabaseTable(tableName = "application_config")
public class ApplicationConfig extends ServerAwareEntity{
    @DatabaseField(foreign = true, foreignAutoRefresh =  true)
    private AppearanceType appearanceType;
    @DatabaseField
    private int phoneId;

    public AppearanceType getAppearanceType() {
        return appearanceType;
    }

    public void setAppearanceType(AppearanceType appearanceType) {
        this.appearanceType = appearanceType;
    }

    public int getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(int phoneId) {
        this.phoneId = phoneId;
    }
}
