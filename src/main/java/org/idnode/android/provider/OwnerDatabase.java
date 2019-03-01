package org.idnode.android.provider;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by bhadoria on 2/28/19.
 */

@Database(entities = {EcdsaKey.class, EciesKey.class, History.class}, version = 1)
public abstract class OwnerDatabase extends RoomDatabase {
    public abstract OwnerDao ownerDao();
}
