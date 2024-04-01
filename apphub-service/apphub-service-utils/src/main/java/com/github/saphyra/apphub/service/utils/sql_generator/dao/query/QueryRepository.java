package com.github.saphyra.apphub.service.utils.sql_generator.dao.query;

import org.springframework.data.repository.CrudRepository;

//TODO unit test
interface QueryRepository extends CrudRepository<QueryEntity, String> {
    void deleteByUserId(String userId);
}
