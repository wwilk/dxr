package pl.devoxx.dxr.android.rest;

import org.springframework.web.client.RestTemplate;

/**
 * Created by wilk on 12/03/15.
 */
public interface RestConfig {
    String getServerUrl();
    RestTemplate getTemplate();
}
