package com.github.saphyra.apphub.service.calendar.dao.event;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface EventRepository extends CrudRepository<EventEntity, String> {
    void deleteByUserId(String userId);

    List<EventEntity> getByUserId(String userId);
}
