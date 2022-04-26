package com.github.saphyra.apphub.service.community.friendship.dao.friend;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

interface FriendshipRepository extends CrudRepository<FriendshipEntity, String> {
    @Query("DELETE FriendshipEntity e WHERE (e.userId = :userId AND e.friendId = :friendId) OR (e.userId = :friendId AND e.friendId = :userId)")
    @Modifying
    void deleteByUserIdAndFriendId(@Param("userId") String userId, @Param("friendId") String friendId);

    @Query("SELECT e FROM FriendshipEntity e WHERE e.userId = :userId OR e.friendId = :userId")
    List<FriendshipEntity> getByUserIdOrFriendId(String userId);

    @Query("SELECT e FROM FriendshipEntity e WHERE (e.userId = :userId AND e.friendId = :friendId) OR (e.userId = :friendId AND e.friendId = :userId)")
    Optional<FriendshipEntity> findByUserIdAndFriendId(@Param("userId") String userId, @Param("friendId") String friendId);

    @Modifying
    @Query("DELETE FriendshipEntity e WHERE e.userId = :userId OR e.friendId = :userId")
    void deleteByUserId(@Param("userId") String userId);
}
