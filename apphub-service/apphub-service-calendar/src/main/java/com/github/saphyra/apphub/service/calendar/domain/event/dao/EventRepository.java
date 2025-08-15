package com.github.saphyra.apphub.service.calendar.domain.event.dao;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

//TODO unit test
interface EventRepository extends CrudRepository<EventEntity, String> {
    void deleteByUserId(String userId);

    List<EventEntity> getByUserId(String userId);

    void deleteByUserIdAndEventId(String userId, String eventId);
}
