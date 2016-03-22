package pl.devoxx.dxr.android.nfc_relation;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import pl.devoxx.dxr.android.core.ServerAwareEntity;
import pl.devoxx.dxr.api.nfc_relation.NfcRelationDto;

/**
 * Created by wilk on 11/06/15.
 */
@DatabaseTable(tableName = "nfcRelation")
public class NfcRelation extends ServerAwareEntity{

    @DatabaseField
    private String userId;
    @DatabaseField
    private String nfcTagId;
    @DatabaseField
    private boolean persistedOnServer;

    public NfcRelation() {
    }

    public NfcRelation(NfcRelationDto dto){
        this(dto.getUserId(), dto.getNfcTagId());
    }

    public NfcRelation(String userId, String nfcTagId){
        this.userId = userId;
        this.nfcTagId = nfcTagId;
    }

    public String getNfcTagId() {
        return nfcTagId;
    }

    public void setNfcTagId(String nfcTagId) {
        this.nfcTagId = nfcTagId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isPersistedOnServer() {
        return persistedOnServer;
    }

    public void setPersistedOnServer(boolean persistedOnServer) {
        this.persistedOnServer = persistedOnServer;
    }

    public NfcRelationDto toDto(){
        return new NfcRelationDto(userId, nfcTagId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NfcRelation)) return false;

        NfcRelation that = (NfcRelation) o;

        if(getServerUrl() != null ? !getServerUrl().equals(that.getServerUrl()) : that.getServerUrl() != null)
            return false;
        if (getUserId() != null ? !getUserId().equals(that.getUserId()) : that.getUserId() != null)
            return false;
        return !(getNfcTagId() != null ? !getNfcTagId().equals(that.getNfcTagId()) : that.getNfcTagId() != null);

    }

    @Override
    public int hashCode() {
        int result = getUserId() != null ? getUserId().hashCode() : 0;
        result = 31 * result + (getNfcTagId() != null ? getNfcTagId().hashCode() : 0);
        result = 31 * result + (getServerUrl() != null ? getServerUrl().hashCode() : 0);
        return result;
    }
}

