package com.github.saphyra.apphub.service.notebook.service.checklist;

import com.github.saphyra.apphub.api.feature.notebook.model.checklist.AddChecklistItemRequest;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ChecklistItemFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ContentFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ParentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ChecklistItemCrudService {
    private final ListItemDao listItemDao;
    private final ChecklistItemFactory checklistItemFactory;
    private final ContentFactory contentFactory;
    private final UuidConverter uuidConverter;

    public void addChecklistItem(UUID userId, UUID listItemId, AddChecklistItemRequest request) {
        ValidationUtil.notNull(request.getContent(), "content");
        ValidationUtil.notNull(request.getIndex(), "index");

        listItemDao.findByIdValidated(userId, listItemId);

        List<Content> contents = listItemDao.getContents(userId, listItemId, ParentType.CHECKLIST_ITEM);
        ChecklistItem checklistItem = checklistItemFactory.create(userId, listItemId, false, request.getIndex());
        contents.add(contentFactory.create(userId, listItemId, ParentType.CHECKLIST_ITEM, checklistItem.getChecklistItemId(), request.getContent()));

        listItemDao.saveContents(checklistItem.getUserId(), checklistItem.getListItemId(), contents);
    }


    public void updateContent(UUID userId, UUID listItemId, UUID checklistItemId, String contentString) {
        ValidationUtil.notNull(contentString, "content");

        List<Content> contents = listItemDao.getContents(userId, listItemId, ParentType.CHECKLIST_ITEM);
        Content content = contents.stream()
            .filter(c -> c.getContent().containsKey(uuidConverter.convertDomain(checklistItemId)))
            .findAny()
            .orElseThrow(() -> ExceptionFactory.notFound("Content not found for checklistItemId " + checklistItemId));

        content.add(uuidConverter.convertDomain(checklistItemId), contentString);

        listItemDao.saveContents(userId, listItemId, contents);
    }

    public void deleteChecklistItem(UUID userId, UUID listItemId, UUID checklistItemId) {
        listItemDao.findByIdValidated(userId, listItemId);

        listItemDao.deleteChecklistItem(userId, listItemId, checklistItemId);
    }

    public void updateStatus(UUID userId, UUID listItemId, UUID checklistItemId, Boolean status){
        ValidationUtil.notNull(status, "status");

        ChecklistItem checklistItem = listItemDao.findChecklistItemValidated(userId, listItemId, checklistItemId);

        checklistItem.setChecked(status);

        listItemDao.saveChecklistItem(checklistItem);
    }

    public void deleteCheckedItems(UUID userId, UUID listItemId) {
        List<ChecklistItem> toDelete = listItemDao.getChecklistItems(userId, listItemId)
            .stream()
            .filter(ChecklistItem::isChecked)
            .toList();

        listItemDao.deleteChecklistItems(toDelete);
    }
}