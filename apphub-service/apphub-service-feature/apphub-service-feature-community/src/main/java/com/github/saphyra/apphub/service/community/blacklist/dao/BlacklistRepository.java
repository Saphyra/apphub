package com.github.saphyra.apphub.service.community.blacklist.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

interface BlacklistRepository extends CrudRepository<BlacklistEntity, String> {
    List<BlacklistEntity> getByUserId(String userId);

    @Query("SELECT e FROM BlacklistEntity e WHERE (e.userId = :userId AND e.blockedUserId = :blockedUserId) OR (e.userId = :blockedUserId AND e.blockedUserId = :userId)")
    Optional<BlacklistEntity> findByUserIdOrBlockedUserId(@Param("userId") String userId, @Param("blockedUserId") String blockedUserId);

    @Query("SELECT e FROM BlacklistEntity e WHERE e.userId = :userId OR e.blockedUserId = :userId")
    List<BlacklistEntity> getByUserIdOrBlockedUserId(@Param("userId") String userId);

    @Query("DELETE BlacklistEntity e WHERE e.userId = :userId OR e.blockedUserId = :userId")
    @Modifying
    void deleteByUserId(@Param("userId") String userId);
}
