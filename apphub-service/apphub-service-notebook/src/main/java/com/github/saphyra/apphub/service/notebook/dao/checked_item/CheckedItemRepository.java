package com.github.saphyra.apphub.service.notebook.dao.checked_item;

import org.springframework.data.repository.CrudRepository;

//TODO unit test
interface CheckedItemRepository extends CrudRepository<CheckedItemEntity, String> {
    void deleteByUserId(String userId);
}
