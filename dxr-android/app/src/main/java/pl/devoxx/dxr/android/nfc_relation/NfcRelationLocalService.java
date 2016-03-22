package pl.devoxx.dxr.android.nfc_relation;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.List;

import pl.devoxx.dxr.android.config.ServerUrlProvider;
import pl.devoxx.dxr.android.core.ServerAwareLocalService;
import pl.devoxx.dxr.api.results.CreateRelationResult;

/**
 * Created by wilk on 11/06/15.
 */
public class NfcRelationLocalService extends ServerAwareLocalService<NfcRelation>{

    public NfcRelationLocalService(ServerUrlProvider serverUrlProvider, RuntimeExceptionDao<NfcRelation, Integer> dao){
        super(serverUrlProvider, dao);
    }

    public CreateRelationResult save(NfcRelation relation){
        NfcRelation existingRelation = findByNfcTagId(relation.getNfcTagId());
        if(existingRelation != null) {
            if (existingRelation.getUserId().equals(relation.getUserId())) {
                return CreateRelationResult.RELATION_ALREADY_EXISTS;
            } else{
                return CreateRelationResult.TAG_ALREADY_HAS_ANOTHER_RELATION;
            }
        }
        super.create(relation);
        return CreateRelationResult.SUCCESS;
    }

    public NfcRelation findByNfcTagId(String nfcTagId){
        try{
            Where<NfcRelation, Integer> query = dao.queryBuilder().where()
                    .eq("nfcTagId", nfcTagId);
            return dao.queryForFirst(prepareWithServerUrl(query));
        } catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public boolean existsRelation(String userId, String nfcTagId){
        try{
            Where<NfcRelation, Integer> queryBuilder = dao.queryBuilder().where()
                    .eq("userId", userId).and()
                    .eq("nfcTagId", nfcTagId);
            return dao.queryForFirst(prepareWithServerUrl(queryBuilder)) != null;
        } catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public List<NfcRelation> getAllRelationsNotPersistedOnServer(){
        try {
            Where<NfcRelation, Integer> where = dao.queryBuilder().where().eq("persistedOnServer", false);

            return dao.query(prepareWithServerUrl(where));
        } catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void update(NfcRelation relation){
        dao.update(relation);
    }
}
