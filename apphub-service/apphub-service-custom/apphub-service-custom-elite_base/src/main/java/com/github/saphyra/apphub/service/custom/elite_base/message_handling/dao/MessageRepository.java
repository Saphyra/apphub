package com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface MessageRepository extends CrudRepository<MessageEntity, String> {
    @Modifying
    @Query("UPDATE MessageEntity e SET e.status=:updatedStatus WHERE e.status=:currentStatus")
    @Transactional
    void setStatus(@Param("currentStatus") MessageStatus currentStatus, @Param("updatedStatus") MessageStatus updatedStatus);

    @Transactional
    @Query("DELETE MessageEntity e WHERE e.createdAt < :createdAt AND e.status in :statuses")
    @Modifying
    void deleteByCreatedAtBeforeAndStatusIn(String createdAt, List<MessageStatus> statuses);

    List<MessageEntity> getByCreatedAtBeforeAndStatusOrderByCreatedAtAsc(String createdAt, MessageStatus status, Pageable page);
}
