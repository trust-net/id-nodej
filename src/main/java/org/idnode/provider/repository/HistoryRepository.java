package org.idnode.provider.repository;

import org.idnode.provider.entity.EcdsaKey;
import org.idnode.provider.entity.EciesKey;
import org.idnode.provider.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HistoryRepository extends JpaRepository<History, History.HistoryPk> {
    @Query("SELECT * FROM history WHERE user LIKE :user AND shard LIKE :shard")
    History getHistoryByUserAndShard(@Param("user")String user, @Param("shard")String shard);
}
