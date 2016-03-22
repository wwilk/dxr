package pl.devoxx.dxr.android.rest;

import com.fasterxml.jackson.databind.DeserializationFeature;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;

import pl.devoxx.dxr.android.config.PhoneIdHolder;
import pl.devoxx.dxr.android.config.ServerUrlProvider;

/**
 * Created by wilk on 14/12/15.
 */
public class DefaultRestConfig implements RestConfig{

    private final RestTemplate restTemplate;
    private final ServerUrlProvider serverUrlProvider;

    public DefaultRestConfig(int readTimeout, int connectTimeout, ServerUrlProvider serverUrlProvider){
        this.serverUrlProvider = serverUrlProvider;
        this.restTemplate = new RestTemplate();
        SimpleClientHttpRequestFactory rf =
                (SimpleClientHttpRequestFactory) restTemplate.getRequestFactory();
        rf.setReadTimeout(readTimeout);
        rf.setConnectTimeout(connectTimeout);
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.getObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        if(restTemplate.getInterceptors() == null){
            restTemplate.setInterceptors(new ArrayList<ClientHttpRequestInterceptor>(1));
        }
        restTemplate.getInterceptors().add(new AndroidIdHttpInterceptor());
        restTemplate.getMessageConverters().add(converter);
    }

    @Override
    public RestTemplate getTemplate(){
        return restTemplate;
    }

    @Override
    public String getServerUrl(){
        return serverUrlProvider.getServerUrl();
    }

    private static class AndroidIdHttpInterceptor implements ClientHttpRequestInterceptor{
        @Override
        public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
            HttpRequest wrapper = new HttpRequestWrapper(httpRequest);
            wrapper.getHeaders().set("dxr-phone-id", PhoneIdHolder.getAsString());
            return clientHttpRequestExecution.execute(wrapper, bytes);
        }
    }
}
