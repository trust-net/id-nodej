package org.idnode.provider.repository;

import org.idnode.provider.entity.EcdsaKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EcdsaKeyRepository extends JpaRepository<EcdsaKey, String> {
    @Query("SELECT * FROM ecdsakeys WHERE user LIKE :user")
    EcdsaKey getEcdsaKeyByUser(@Param("user") String user);
}
