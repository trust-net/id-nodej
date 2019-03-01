package org.idnode.android.provider;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by bhadoria on 2/28/19.
 */

@Entity(tableName = "ecdsakeys")
public class EcdsaKey {
    @PrimaryKey
    @NonNull
    public String user;

    @ColumnInfo(name="key")
    public String key;

    public EcdsaKey(String user, String key) {
        this.user = user;
        this.key = key;
    }
}
