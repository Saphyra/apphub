package com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.deprecated_dao;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
@Deprecated(forRemoval = true)

interface DeprecatedEventLabelMappingRepository extends CrudRepository<DeprecatedEventLabelMappingEntity, String> {
    void deleteByUserId(String userId);

    void deleteByUserIdAndEventId(String userId, String eventId);

    List<DeprecatedEventLabelMappingEntity> getByEventId(String eventId);

    void deleteByUserIdAndLabelId(String userId, String labelId);

    List<DeprecatedEventLabelMappingEntity> getByUserIdAndLabelId(String userId, String labelId);
}
