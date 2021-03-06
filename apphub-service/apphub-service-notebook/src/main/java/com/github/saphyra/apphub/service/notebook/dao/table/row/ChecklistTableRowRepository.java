package com.github.saphyra.apphub.service.notebook.dao.table.row;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

interface ChecklistTableRowRepository extends CrudRepository<ChecklistTableRowEntity, String> {
    void deleteByUserId(String userId);

    Optional<ChecklistTableRowEntity> findByParentAndRowIndex(String parent, int rowIndex);

    List<ChecklistTableRowEntity> getByParent(String parent);

    void deleteByParentAndRowIndexGreaterThanEqual(String parent, int rowIndex);

    void deleteByParent(String parent);
}
