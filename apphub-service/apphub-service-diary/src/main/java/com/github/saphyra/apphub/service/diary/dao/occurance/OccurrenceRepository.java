package com.github.saphyra.apphub.service.diary.dao.occurance;

import org.springframework.data.repository.CrudRepository;

//TODO unit test
interface OccurrenceRepository extends CrudRepository<OccurrenceEntity, String> {
    void deleteByUserId(String userId);
}
