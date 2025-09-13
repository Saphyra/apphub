package com.github.saphyra.apphub.service.calendar.domain.label.dao;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface LabelRepository extends CrudRepository<LabelEntity, String> {
    void deleteByUserId(String userId);

    List<LabelEntity> getByUserId(String userId);

    void deleteByUserIdAndLabelId(String userId, String labelId);
}
