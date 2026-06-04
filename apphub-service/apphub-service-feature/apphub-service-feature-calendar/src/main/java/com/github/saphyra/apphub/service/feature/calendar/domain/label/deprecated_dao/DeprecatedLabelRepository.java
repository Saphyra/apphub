package com.github.saphyra.apphub.service.feature.calendar.domain.label.deprecated_dao;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
@Deprecated(forRemoval = true)

interface DeprecatedLabelRepository extends CrudRepository<DeprecatedLabelEntity, String> {
    void deleteByUserId(String userId);

    List<DeprecatedLabelEntity> getByUserId(String userId);

    void deleteByUserIdAndLabelId(String userId, String labelId);
}
