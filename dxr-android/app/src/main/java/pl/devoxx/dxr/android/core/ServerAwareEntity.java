package pl.devoxx.dxr.android.core;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by wilk on 15/12/15.
 */
public class ServerAwareEntity {

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String serverUrl;

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServerAwareEntity)) return false;

        ServerAwareEntity that = (ServerAwareEntity) o;

        return getId() == that.getId();

    }

    @Override
    public int hashCode() {
        return getId();
    }
}
