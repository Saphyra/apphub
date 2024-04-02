package com.github.saphyra.apphub.service.utils.sql_generator.dao.query;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

//TODO unit test
interface QueryRepository extends CrudRepository<QueryEntity, String> {
    void deleteByUserId(String userId);

    List<QueryEntity> getByUserId(String userId);
}
