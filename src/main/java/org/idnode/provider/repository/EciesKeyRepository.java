package org.idnode.provider.repository;

import org.idnode.provider.entity.EciesKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EciesKeyRepository extends JpaRepository<EciesKey, String> {
    @Query("SELECT * FROM ecieskeys WHERE user LIKE :user")
    EciesKey getEciesKeyByUser(@Param("user") String user);
}
