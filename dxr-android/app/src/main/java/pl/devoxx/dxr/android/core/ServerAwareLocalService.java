package pl.devoxx.dxr.android.core;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.List;

import pl.devoxx.dxr.android.config.ServerUrlProvider;

/**
 * Created by wilk on 15/12/15.
 */
public abstract class ServerAwareLocalService<E extends ServerAwareEntity>{

    protected final ServerUrlProvider serverUrlProvider;
    protected final RuntimeExceptionDao<E, Integer> dao;

    protected ServerAwareLocalService(ServerUrlProvider serverUrlProvider, RuntimeExceptionDao<E, Integer> dao){
        this.serverUrlProvider = serverUrlProvider;
        this.dao = dao;
    }

    public void create(E entity){
        if(entity.getServerUrl() == null) {
            entity.setServerUrl(serverUrlProvider.getServerUrl());
        }
        dao.create(entity);
    }

    public void update(E entity){
        dao.update(entity);
    }

    public List<E> findAll(){
        return dao.query(prepareWithServerUrl(dao.queryBuilder()));
    }

    public long countAll(){
        return dao.countOf(prepareWithServerUrl(dao.queryBuilder().setCountOf(true)));
    }

    protected PreparedQuery<E> prepareWithServerUrl(Where<E, Integer> where){
        try {
            return where.and().eq("serverUrl", serverUrlProvider.getServerUrl()).prepare();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected PreparedQuery<E> prepareWithServerUrl(QueryBuilder<E, Integer> builder){
        try {
            return builder.where().eq("serverUrl", serverUrlProvider.getServerUrl()).prepare();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
