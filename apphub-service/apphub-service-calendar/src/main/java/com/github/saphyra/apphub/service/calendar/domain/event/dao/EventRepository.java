package com.github.saphyra.apphub.service.calendar.domain.event.dao;

import org.springframework.data.repository.CrudRepository;

//TODO unit test
interface EventRepository extends CrudRepository<EventEntity, String> {
    void deleteByUserId(String userId);
}
