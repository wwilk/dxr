package pl.devoxx.dxr.android.appearanceType;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import pl.devoxx.dxr.android.core.ServerAwareEntity;
import pl.devoxx.dxr.api.appearance_type.AppearanceTypeDto;

/**
 * Created by wilk on 05/12/15.
 */
@DatabaseTable(tableName = "appearance_type")
public class AppearanceType extends ServerAwareEntity{
    @DatabaseField
    private String name;
    @DatabaseField
    private String email;
    @DatabaseField
    private boolean nfcWriteEnabled;

    public AppearanceType(){

    }

    public AppearanceType(AppearanceTypeDto dto){
        this(dto.getName(), dto.isNfcWriteEnabled(), dto.getEmail());
    }

    public AppearanceType(String name, boolean nfcWriteEnabled, String email){
        this.name = name;
        this.nfcWriteEnabled = nfcWriteEnabled;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isNfcWriteEnabled() {
        return nfcWriteEnabled;
    }

    public void setNfcWriteEnabled(boolean nfcWriteEnabled) {
        this.nfcWriteEnabled = nfcWriteEnabled;
    }

    public AppearanceTypeDto toDto(){
        AppearanceTypeDto dto = new AppearanceTypeDto();
        dto.setName(name);
        dto.setEmail(email);
        dto.setNfcWriteEnabled(nfcWriteEnabled);
        return dto;
    }

}
