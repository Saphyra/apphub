package com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

interface FriendshipRepository extends CrudRepository<FriendshipEntity, String> {
    @Query("SELECT e FROM FriendshipEntity e WHERE e.friend1 = :userId OR e.friend2 = : userId")
    List<FriendshipEntity> getByFriendId(@Param("userId") String userId);

    @Modifying
    @Query("DELETE FROM FriendshipEntity e WHERE e.friend1 = :userId OR e.friend2 = :userId")
    void deleteByFriendId(@Param("userId") String userId);

    @Query("SELECT e FROM FriendshipEntity e WHERE (e.friend1 = :friend1 AND e.friend2 = :friend2) OR (e.friend1 = :friend2 AND e.friend2 = :friend1)")
    Optional<FriendshipEntity> findByFriendIds(@Param("friend1") String friend1, @Param("friend2") String friend2);
}
