package com.github.saphyra.apphub.service.utils.sql_generator.dao.segment_value;

import org.springframework.data.repository.CrudRepository;

//TODO unit test
interface SegmentValueRepository extends CrudRepository<SegmentValueEntity, String> {
    void deleteByUserId(String userId);
}
