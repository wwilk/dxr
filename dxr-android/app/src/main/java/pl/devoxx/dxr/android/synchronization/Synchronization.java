package pl.devoxx.dxr.android.synchronization;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import pl.devoxx.dxr.android.core.ServerAwareEntity;

/**
 * Created by wilk on 22/03/15.
 */
@DatabaseTable(tableName = "synchronization")
public class Synchronization extends ServerAwareEntity{

    @DatabaseField
    private Date date;

    public Synchronization(){}

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
