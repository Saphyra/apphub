package com.github.saphyra.apphub.service.notebook.dao.column_type;

import org.springframework.data.repository.CrudRepository;

interface ColumnTypeRepository extends CrudRepository<ColumnTypeEntity, String> {
    void deleteByUserId(String userId);
}
