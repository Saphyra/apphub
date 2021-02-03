package com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface FriendshipRepository extends CrudRepository<FriendshipEntity, String> {
    @Query("SELECT e FROM FriendshipEntity e WHERE e.friend1 = :userId OR e.friend2 = : userId")
    List<FriendshipEntity> getByFriendId(@Param("userId") String userId);

    @Modifying
    @Query("DELETE FROM FriendshipEntity e WHERE e.friend1 = :userId OR e.friend2 = :userId")
    void deleteByFriendId(@Param("userId") String userId);
}
