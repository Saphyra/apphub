package com.github.saphyra.apphub.service.notebook.dao.table.head;

import org.springframework.data.repository.CrudRepository;

//TODO unit test
interface TableHeadRepository extends CrudRepository<TableHeadEntity, String> {
    void deleteByUserId(String userId);
}
