package com.github.saphyra.apphub.service.notebook.dao.list_item;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface ListItemRepository extends CrudRepository<ListItemEntity, String> {
    List<ListItemEntity> getByUserIdAndType(String userId, ListItemType type);

    List<ListItemEntity> getByParent(String parent);

    void deleteByUserId(String userID);
}
