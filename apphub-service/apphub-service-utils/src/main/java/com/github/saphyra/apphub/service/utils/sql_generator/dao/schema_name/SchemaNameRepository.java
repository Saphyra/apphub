package com.github.saphyra.apphub.service.utils.sql_generator.dao.schema_name;

import org.springframework.data.repository.CrudRepository;

//TODO unit test
interface SchemaNameRepository extends CrudRepository<SchemaNameEntity, String> {
    void deleteByUserId(String userId);
}
