package com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

//TODO unit test
interface FriendRequestRepository extends CrudRepository<FriendRequestEntity, String> {
    @Modifying
    @Query("DELETE FROM FriendRequestEntity e WHERE e.senderId = :userId OR e.friendId = :userId")
    void deleteBySenderIdOrFriendId(@Param("userId") String userId);

    @Query("SELECT e FROM FriendRequestEntity e WHERE (e.senderId = :senderId AND e.friendId = :friendId) OR (e.senderId = :friendId AND e.friendId = :senderId)")
    Optional<FriendRequest> findBySenderIdAndFriendId(@Param("senderId") String senderId, @Param("friendId") String friendId);
}
