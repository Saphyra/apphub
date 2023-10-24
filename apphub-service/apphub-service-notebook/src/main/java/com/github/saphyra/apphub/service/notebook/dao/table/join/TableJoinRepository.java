package com.github.saphyra.apphub.service.notebook.dao.table.join;

import com.github.saphyra.apphub.lib.common_util.ForRemoval;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@ForRemoval("notebook-redesign")
interface TableJoinRepository extends CrudRepository<TableJoinEntity, String> {
    void deleteByUserId(String userId);

    List<TableJoinEntity> getByParent(String parent);
}
