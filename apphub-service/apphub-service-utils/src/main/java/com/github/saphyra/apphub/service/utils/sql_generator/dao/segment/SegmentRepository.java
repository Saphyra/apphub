package com.github.saphyra.apphub.service.utils.sql_generator.dao.segment;

import org.springframework.data.repository.CrudRepository;

//TODO unit test
interface SegmentRepository extends CrudRepository<SegmentEntity, String> {
    void deleteByUserId(String userId);
}
