package com.github.saphyra.apphub.service.notebook.dao.checklist_item;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

//TODO unit test
interface ChecklistItemRepository extends CrudRepository<ChecklistItemEntity, String> {
    void deleteByUserId(String userId);

    List<ChecklistItemEntity> getByParent(String parent);
}
