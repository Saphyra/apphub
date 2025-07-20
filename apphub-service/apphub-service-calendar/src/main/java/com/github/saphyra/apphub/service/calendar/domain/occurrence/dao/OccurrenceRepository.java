package com.github.saphyra.apphub.service.calendar.domain.occurrence.dao;

import org.springframework.data.repository.CrudRepository;

//TODO unit test
interface OccurrenceRepository extends CrudRepository<OccurrenceEntity, String> {
    void deleteByUserId(String userId);
}
