package org.idnode.android.provider;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by bhadoria on 2/28/19.
 */

@Entity(primaryKeys = {"user", "shard"}, tableName = "history")
public class History {
    @ColumnInfo(name="user")
    @NonNull
    String user;

    @ColumnInfo(name="shard")
    @NonNull
    String shard;

    @ColumnInfo(name="history")
    String history;

    public History(String user, String shard, String history) {
        this.user = user;
        this.shard = shard;
        this.history = history;
    }
}
