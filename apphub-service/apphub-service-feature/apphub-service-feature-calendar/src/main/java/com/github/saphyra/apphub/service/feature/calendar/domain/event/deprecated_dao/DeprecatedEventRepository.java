package com.github.saphyra.apphub.service.feature.calendar.domain.event.deprecated_dao;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
@Deprecated(forRemoval = true)

interface DeprecatedEventRepository extends CrudRepository<DeprecatedEventEntity, String> {
    void deleteByUserId(String userId);

    List<DeprecatedEventEntity> getByUserId(String userId);

    void deleteByUserIdAndEventId(String userId, String eventId);
}
