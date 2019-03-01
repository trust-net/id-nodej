package org.idnode.android.provider;

import android.arch.persistence.room.Room;
import android.content.Context;

import org.idnode.identity.Provider;

/**
 * Created by bhadoria on 2/28/19.
 */

public class DbProvider implements Provider {
    private static final String TAG = "DbProvider";
    private OwnerDatabase db;

    public DbProvider(Context ctx) {
        db = Room.databaseBuilder(ctx, OwnerDatabase.class, "idnode-client").build();
    }

    @Override
    public String getECDSAKeyForUser(String user) {
        EcdsaKey entity = db.ownerDao().getEcdsaKeyByUser(user);
        if (entity != null) {
            return entity.key;
        } else {
            return null;
        }
    }

    @Override
    public void saveECDSAKeyForUser(String user, String key) {
        db.ownerDao().upsert(new EcdsaKey(user, key));
    }

    @Override
    public String getECIESKeyForUser(String user) {
        EciesKey entity = db.ownerDao().getEciesKeyByUser(user);
        if (entity != null) {
            return entity.key;
        } else {
            return null;
        }
    }

    @Override
    public void saveECIESKeyForUser(String user, String key) {
        db.ownerDao().upsert(new EciesKey(user, key));
    }

    @Override
    public String getSequenceHistoryForUser(String user, String shardId) {
        History entity = db.ownerDao().getHistoryByUserAndShard(user, shardId);
        if (entity != null) {
            return entity.history;
        } else {
            return null;
        }
    }

    @Override
    public void saveSequenceHistoryForUser(String user, String shardId, String history) {
        db.ownerDao().upsert(new History(user, shardId, history));
    }
}
