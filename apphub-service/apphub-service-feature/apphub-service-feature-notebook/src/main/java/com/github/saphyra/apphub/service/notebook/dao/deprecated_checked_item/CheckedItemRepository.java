package com.github.saphyra.apphub.service.notebook.dao.deprecated_checked_item;

import org.springframework.data.repository.CrudRepository;

@Deprecated(forRemoval = true)
interface CheckedItemRepository extends CrudRepository<CheckedItemEntity, String> {
    void deleteByUserId(String userId);
}
