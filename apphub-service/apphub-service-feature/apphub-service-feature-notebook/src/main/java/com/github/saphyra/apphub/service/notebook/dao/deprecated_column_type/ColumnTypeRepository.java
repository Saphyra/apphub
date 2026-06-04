package com.github.saphyra.apphub.service.notebook.dao.deprecated_column_type;

import org.springframework.data.repository.CrudRepository;

@Deprecated(forRemoval = true)
interface ColumnTypeRepository extends CrudRepository<ColumnTypeEntity, String> {
    void deleteByUserId(String userId);
}
