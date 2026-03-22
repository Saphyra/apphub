package com.github.saphyra.apphub.service.user.authentication.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

interface AccessTokenRepository extends CrudRepository<AccessTokenEntity, String> {
    @Transactional
    void deleteByPersistentAndLastAccessBefore(boolean persistent, LocalDateTime expiration);

    @Transactional
    @Modifying
    @Query("UPDATE AccessTokenEntity e SET e.lastAccess = :currentDate WHERE e.accessTokenId = :accessTokenId")
    void updateLastAccess(@Param("accessTokenId") String accessTokenId, @Param("currentDate") LocalDateTime currentDate);

    @Transactional
    void deleteByAccessTokenIdAndUserId(String accessTokenId, String userId);

    void deleteByUserId(String userId);

    List<AccessTokenEntity> getByUserId(String userId);
}
