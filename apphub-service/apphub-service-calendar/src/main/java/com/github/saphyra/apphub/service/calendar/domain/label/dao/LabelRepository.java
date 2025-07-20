package com.github.saphyra.apphub.service.calendar.domain.label.dao;

import org.springframework.data.repository.CrudRepository;

//TODO unit test
interface LabelRepository extends CrudRepository<LabelEntity, String> {
    void deleteByUserId(String userId);
}
