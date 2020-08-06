package com.github.saphyra.apphub.service.notebook.dao.table.join;

import org.springframework.data.repository.CrudRepository;

//TODO unit test
interface TableJoinRepository extends CrudRepository<TableJoinEntity, String> {
    void deleteByUserId(String userId);
}
