package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.deprecated_dao;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
@Deprecated(forRemoval = true)

interface DeprecatedOccurrenceRepository extends CrudRepository<DeprecatedOccurrenceEntity, String> {
    void deleteByUserId(String userId);

    void deleteByUserIdAndEventId(String userId, String eventId);

    List<DeprecatedOccurrenceEntity> getByEventId(String eventId);

    List<DeprecatedOccurrenceEntity> getByUserId(String userId);
}
