package pl.devoxx.dxr.android.appearance;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import pl.devoxx.dxr.android.core.ServerAwareEntity;
import pl.devoxx.dxr.api.appearance.AppearanceDto;

/**
 * Created by wilk on 21/03/15.
 */
@DatabaseTable(tableName = "appearance")
public class Appearance extends ServerAwareEntity{

    @DatabaseField
    private Date date;
    @DatabaseField
    private String userId;
    @DatabaseField
    private boolean persistedOnServer;
    @DatabaseField
    private String appearanceTypeName;

    public Appearance(){

    }

    public Appearance(AppearanceDto dto){
        this.date = dto.getDate();
        this.userId = dto.getUserId();
        this.appearanceTypeName = dto.getAppearanceTypeName();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isPersistedOnServer() {
        return persistedOnServer;
    }

    public void setPersistedOnServer(boolean persistedOnServer) {
        this.persistedOnServer = persistedOnServer;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAppearanceTypeName() {
        return appearanceTypeName;
    }

    public void setAppearanceTypeName(String appearanceTypeName) {
        this.appearanceTypeName = appearanceTypeName;
    }

    public AppearanceDto toDto(){
        AppearanceDto dto = new AppearanceDto();
        dto.setUserId(userId);
        dto.setDate(date);
        dto.setAppearanceTypeName(appearanceTypeName);
        return dto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Appearance that = (Appearance) o;

        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (appearanceTypeName != null ? !appearanceTypeName.equals(that.appearanceTypeName) : that.appearanceTypeName != null) return false;
        return !(getServerUrl() != null ? !getServerUrl().equals(that.getServerUrl()) : that.getServerUrl() != null);
    }

    @Override
    public int hashCode() {
        int result = date != null ? date.hashCode() : 0;
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (appearanceTypeName != null ? appearanceTypeName.hashCode() : 0);
        result = 31 * result + (getServerUrl() != null ? getServerUrl().hashCode() : 0);
        return result;
    }
}
