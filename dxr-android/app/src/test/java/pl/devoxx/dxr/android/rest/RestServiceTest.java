package pl.devoxx.dxr.android.rest;

import com.fasterxml.jackson.databind.DeserializationFeature;

import org.hamcrest.CustomMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.net.SocketTimeoutException;

import pl.devoxx.dxr.android.BuildConfig;
import pl.devoxx.dxr.android.CustomTestRunner;

/**
 * Created by wilk on 12/04/15.
 */
@RunWith(CustomTestRunner.class)
@Config(constants = BuildConfig.class)
public class RestServiceTest {

    private RestService restService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test(timeout = 2000)
    public void connect_timeout(){
        RestConfig config = new RestConfig(){
            @Override
            public RestTemplate getTemplate() {
                RestTemplate restTemplate = new RestTemplate();
                SimpleClientHttpRequestFactory rf =
                        (SimpleClientHttpRequestFactory) restTemplate.getRequestFactory();
                rf.setReadTimeout(1000);
                rf.setConnectTimeout(1000);
                MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
                converter.getObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                restTemplate.getMessageConverters().add(converter);
                return restTemplate;
            }

            @Override
            public String getServerUrl() {
                return "http://www.google.com:81/";
            }
        };

        restService = new StringRestService(config);

        expectedException.expectCause(new CustomMatcher<Throwable>("expected timeout") {
            @Override
            public boolean matches(Object o) {
                return o instanceof SocketTimeoutException;
            }
        });

        restService.get("");
    }

    @Test(timeout = 2000)
    public void read_timeout(){
        RestConfig config = new RestConfig(){
            @Override
            public RestTemplate getTemplate() {
                RestTemplate restTemplate = new RestTemplate();
                SimpleClientHttpRequestFactory rf =
                        (SimpleClientHttpRequestFactory) restTemplate.getRequestFactory();
                rf.setReadTimeout(1000);
                rf.setConnectTimeout(1000);
                MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
                converter.getObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                restTemplate.getMessageConverters().add(converter);
                return restTemplate;
            }

            @Override
            public String getServerUrl() {
                return "http://10.255.255.1/";
            }
        };

        restService = new StringRestService(config);

        expectedException.expectCause(new CustomMatcher<Throwable>("expected timeout") {
            @Override
            public boolean matches(Object o) {
                return o instanceof SocketTimeoutException;
            }
        });

        restService.get("");
    }

    private class StringRestService extends RestService<String>{
        public StringRestService(RestConfig config){
            super(String.class, "", String[].class, config);
        }
    }
}
