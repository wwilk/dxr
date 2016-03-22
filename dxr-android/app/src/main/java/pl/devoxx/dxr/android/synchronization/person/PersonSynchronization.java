package pl.devoxx.dxr.android.synchronization.person;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import pl.devoxx.dxr.android.core.ServerAwareEntity;

/**
 * Created by wilk on 07/12/15.
 */
@DatabaseTable(tableName = "person_synchronization")
public class PersonSynchronization extends ServerAwareEntity{
    @DatabaseField
    private Date date;
    @DatabaseField
    private int synchronizedPeopleCount;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getSynchronizedPeopleCount() {
        return synchronizedPeopleCount;
    }

    public void setSynchronizedPeopleCount(int synchronizedPeopleCount) {
        this.synchronizedPeopleCount = synchronizedPeopleCount;
    }
}
