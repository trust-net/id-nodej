package org.idnode.android.provider;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by bhadoria on 2/28/19.
 */

@Dao
public interface OwnerDao {

    @Query("SELECT * FROM ecdsakeys")
    List<EcdsaKey> getAllEcdsaKeys();

    @Query("SELECT * FROM ecdsakeys WHERE user LIKE :user")
    EcdsaKey getEcdsaKeyByUser(String user);

    @Query("SELECT * FROM ecieskeys WHERE user LIKE :user")
    EciesKey getEciesKeyByUser(String user);

    @Query("SELECT * FROM history WHERE user LIKE :user AND shard LIKE :shard")
    History getHistoryByUserAndShard(String user, String shard);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(EcdsaKey ecdsaKey);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(EciesKey eciesKey);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(History history);
}
