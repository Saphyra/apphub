package com.github.saphyra.apphub.service.community.friendship.dao.request;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

//TODO unit test
interface FriendRequestRepository extends CrudRepository<FriendRequestEntity, String> {
    @Modifying
    @Query("DELETE FriendRequestEntity e WHERE (e.senderId = :senderId AND e.receiverId = :receiverId) OR (e.senderId = :receiverId AND e.receiverId = :senderId)")
    void deleteBySenderIdAndReceiverId(@Param("senderId") String senderId, @Param("receiverId") String receiverId);

    @Query("SELECT e FROM FriendRequestEntity e WHERE e.senderId = :userId OR e.receiverId = :userId")
    List<FriendRequestEntity> getBySenderIdOrReceiverId(UUID userId);

    List<FriendRequestEntity> getBySenderId(String senderId);

    List<FriendRequestEntity> getByReceiverId(String receiverId);

    @Query("SELECT e FROM FriendRequestEntity e WHERE (e.senderId = :senderId AND e.receiverId = :receiverId) OR (e.senderId = :receiverId AND e.receiverId = :senderId)")
    Optional<FriendRequestEntity> findBySenderIdAndReceiverId(@Param("senderId") String senderId, @Param("receiverId") String receiverId);
}
