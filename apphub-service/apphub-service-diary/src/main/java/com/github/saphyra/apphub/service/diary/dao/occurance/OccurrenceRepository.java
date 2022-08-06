package com.github.saphyra.apphub.service.diary.dao.occurance;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface OccurrenceRepository extends CrudRepository<OccurrenceEntity, String> {
    void deleteByUserId(String userId);

    List<OccurrenceEntity> getByEventId(String eventId);

    void deleteByEventId(String eventId);

    List<OccurrenceEntity> getByUserId(String userId);
}
