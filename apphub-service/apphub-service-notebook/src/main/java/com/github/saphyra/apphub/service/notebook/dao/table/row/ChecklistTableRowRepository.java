package com.github.saphyra.apphub.service.notebook.dao.table.row;

import com.github.saphyra.apphub.lib.common_util.ForRemoval;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@ForRemoval("notebook-redesign")
interface ChecklistTableRowRepository extends CrudRepository<ChecklistTableRowEntity, String> {
    void deleteByUserId(String userId);

    List<ChecklistTableRowEntity> getByParent(String parent);

    void deleteByParent(String parent);
}
