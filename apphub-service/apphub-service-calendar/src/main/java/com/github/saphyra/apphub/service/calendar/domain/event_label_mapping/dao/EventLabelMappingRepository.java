package com.github.saphyra.apphub.service.calendar.domain.event_label_mapping.dao;

import org.springframework.data.repository.CrudRepository;

//TODO unit test
interface EventLabelMappingRepository extends CrudRepository<EventLabelMappingEntity, EventLabelMappingEntity> {
    void deleteByUserId(String userId);
}
