package com.github.saphyra.apphub.service.calendar.domain.occurrence.dao;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

//TODO unit test
interface OccurrenceRepository extends CrudRepository<OccurrenceEntity, String> {
    void deleteByUserId(String userId);

    void deleteByUserIdAndEventId(String userId, String eventId);

    List<OccurrenceEntity> getByEventId(String eventId);

    List<OccurrenceEntity> getByUserId(String userId);
}
