package com.github.saphyra.apphub.service.diary.dao.event;

import org.springframework.data.repository.CrudRepository;

//TODO unit test
interface EventRepository extends CrudRepository<EventEntity, String> {
    void deleteByUserId(String userId);
}
