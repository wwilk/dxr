package pl.devoxx.dxr.android.rest;

import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import pl.devoxx.dxr.api.results.Result;

/**
 * Created by wilk on 20/03/15.
 */
public class RestService<T> {

    private final RestConfig config;
    private final String resourceUrl;
    private final Class<T> dtoClass;
    private final Class<T[]> arrayClass;

    public RestService(Class<T> dtoClass, String resourceUrl, Class<T[]> arrayClass, RestConfig restConfig){
        this.resourceUrl = resourceUrl;
        this.config = restConfig;
        this.arrayClass = arrayClass;
        this.dtoClass = dtoClass;
    }

    public Result post(T arg){
        return config.getTemplate().postForObject(getBaseUrl(), arg, Result.class);
    }

    public T get(String argument){
        return config.getTemplate().getForObject(getBaseUrl() + "/" + argument, dtoClass);
    }

    public List<T> getList(String postfix){
        return Arrays.asList(config.getTemplate().getForObject(getBaseUrl() + postfix, arrayClass));
    }

    public List<T> getChangedSince(Date lastSynchronization){
        String postfix = "changed?lastSynchronizationDate=" + convertToJsonString(lastSynchronization);
        return getList(postfix);
    }

    public <R> R custom(CustomAction<R> action){
        return action.execute(getBaseUrl(), config.getTemplate());
    }

    private String convertToJsonString(Date date){
        if(date == null){
            return "";
        }
        return String.valueOf(date.getTime());
    }

    private String getBaseUrl(){
        return config.getServerUrl() + resourceUrl;
    }

    public interface CustomAction<R>{
        R execute(String baseUrl, RestTemplate template);
    }
}
