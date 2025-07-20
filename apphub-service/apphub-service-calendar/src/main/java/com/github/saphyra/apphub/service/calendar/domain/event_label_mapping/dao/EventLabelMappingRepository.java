package com.github.saphyra.apphub.service.calendar.domain.event_label_mapping.dao;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

//TODO unit test
interface EventLabelMappingRepository extends CrudRepository<EventLabelMappingEntity, EventLabelMappingEntity> {
    void deleteByUserId(String userId);

    void deleteByUserIdAndEventId(String userId, String eventId);

    List<EventLabelMappingEntity> getByEventId(String eventId);
}
