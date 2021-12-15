package com.github.saphyra.apphub.service.user.ban.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

interface BanRepository extends CrudRepository<BanEntity, String> {
    void deleteByUserId(String userId);

    List<BanEntity> getByUserId(String userId);

    @Query("DELETE BanEntity e WHERE e.expiration < :expiration")
    @Modifying
    void deleteExpired(@Param("expiration") LocalDateTime expiration);
}
