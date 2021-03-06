package com.github.saphyra.apphub.service.notebook.dao.table.join;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface TableJoinRepository extends CrudRepository<TableJoinEntity, String> {
    void deleteByUserId(String userId);

    List<TableJoinEntity> getByParent(String parent);
}
