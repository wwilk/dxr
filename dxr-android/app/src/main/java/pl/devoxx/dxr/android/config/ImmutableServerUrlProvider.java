package pl.devoxx.dxr.android.config;

/**
 * Created by wilk on 21/12/15.
 */
public class ImmutableServerUrlProvider implements ServerUrlProvider {

    private final String serverUrl;

    public ImmutableServerUrlProvider(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getServerUrl() {
        return serverUrl;
    }
}
