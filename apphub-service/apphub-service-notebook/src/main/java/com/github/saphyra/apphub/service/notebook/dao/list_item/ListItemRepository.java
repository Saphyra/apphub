package com.github.saphyra.apphub.service.notebook.dao.list_item;

import com.github.saphyra.apphub.api.notebook.model.ListItemType;
import com.github.saphyra.apphub.lib.common_util.ForRemoval;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface ListItemRepository extends CrudRepository<ListItemEntity, String> {
    List<ListItemEntity> getByUserIdAndType(String userId, ListItemType type);

    List<ListItemEntity> getByUserIdAndParent(String userId, String parent);

    void deleteByUserId(String userID);

    List<ListItemEntity> getByUserId(String userId);

    @ForRemoval("custom-table-link-migration")
    List<ListItemEntity> getByType(ListItemType type);
}
