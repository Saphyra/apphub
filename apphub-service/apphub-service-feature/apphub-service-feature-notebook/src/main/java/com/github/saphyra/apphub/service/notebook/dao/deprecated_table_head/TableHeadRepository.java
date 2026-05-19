package com.github.saphyra.apphub.service.notebook.dao.deprecated_table_head;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface TableHeadRepository extends CrudRepository<TableHeadEntity, String> {
    void deleteByUserId(String userId);

    List<TableHeadEntity> getByParent(String parent);
}
