package pl.devoxx.dxr.android.registration;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.List;

import pl.devoxx.dxr.android.appearance.Appearance;
import pl.devoxx.dxr.android.appearance.AppearanceLocalService;
import pl.devoxx.dxr.android.config.ServerUrlProvider;
import pl.devoxx.dxr.android.core.ServerAwareLocalService;
import pl.devoxx.dxr.android.nfc_relation.NfcRelation;
import pl.devoxx.dxr.android.nfc_relation.NfcRelationLocalService;

/**
 * Created by wilk on 22/03/15.
 */
public class PersonLocalService extends ServerAwareLocalService<Person> {
    private final AppearanceLocalService appearanceLocalService;
    private final NfcRelationLocalService nfcRelationLocalService;

    public PersonLocalService(RuntimeExceptionDao<Person, Integer> dao, AppearanceLocalService appearanceLocalService,
                              NfcRelationLocalService nfcRelationLocalService, ServerUrlProvider serverUrlProvider){
        super(serverUrlProvider, dao);
        this.appearanceLocalService = appearanceLocalService;
        this.nfcRelationLocalService = nfcRelationLocalService;
    }

    public Person findByUserId(String userId, String appearanceTypeName){
        Person person = findByUserId(userId);
        if(person == null){
            return null;
        }
        List<Appearance> appearances = appearanceLocalService.getAllUserAppearances(userId, appearanceTypeName);
        person.setAppearances(appearances);
        return person;
    }

    public Person findByUserId(String userId){
        try {
            Where<Person, Integer> where = dao.queryBuilder().where().eq("userId", userId);
            return dao.queryForFirst(prepareWithServerUrl(where));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public Person findByNfcTagId(String nfcTagId, String appearanceTypeName){
        NfcRelation nfcRelation = nfcRelationLocalService.findByNfcTagId(nfcTagId);
        if(nfcRelation == null){
            return null;
        }
        return findByUserId(nfcRelation.getUserId(), appearanceTypeName);
    }
}
