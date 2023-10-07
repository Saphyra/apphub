package com.github.saphyra.apphub.service.notebook.dao.table.row;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

@Deprecated
interface ChecklistTableRowRepository extends CrudRepository<ChecklistTableRowEntity, String> {
    void deleteByUserId(String userId);

    List<ChecklistTableRowEntity> getByParent(String parent);

    void deleteByParent(String parent);
}
