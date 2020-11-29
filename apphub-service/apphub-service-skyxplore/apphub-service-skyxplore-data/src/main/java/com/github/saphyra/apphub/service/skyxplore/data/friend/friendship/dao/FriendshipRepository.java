package com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

//TODO unit test
interface FriendshipRepository extends CrudRepository<FriendshipEntity, String> {
    @Query("SELECT e FROM FriendshipEntity e WHERE e.friend1 = :userId OR e.friend2 = : userId")
    List<FriendshipEntity> getByFriendId(@Param("userId") String userId);
}
