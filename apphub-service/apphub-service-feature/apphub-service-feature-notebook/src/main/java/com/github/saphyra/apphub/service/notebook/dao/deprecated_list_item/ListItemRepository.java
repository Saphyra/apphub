package com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@Deprecated(forRemoval = true)
interface ListItemRepository extends CrudRepository<ListItemEntity, String> {
    List<ListItemEntity> getByUserIdAndType(String userId, ListItemType type);

    List<ListItemEntity> getByUserIdAndParent(String userId, String parent);

    void deleteByUserId(String userID);

    List<ListItemEntity> getByUserId(String userId);
}
