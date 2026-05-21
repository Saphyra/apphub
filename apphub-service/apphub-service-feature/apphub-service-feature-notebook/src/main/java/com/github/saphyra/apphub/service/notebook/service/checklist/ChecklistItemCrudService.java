package com.github.saphyra.apphub.service.notebook.service.checklist;

import com.github.saphyra.apphub.api.feature.notebook.model.checklist.AddChecklistItemRequest;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item.ChecklistItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item.ChecklistItemFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ParentType;
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
    private final ChecklistItemDao checklistItemDao;
    private final ContentDao contentDao;

    public void addChecklistItem(UUID userId, UUID listItemId, AddChecklistItemRequest request) {
        ValidationUtil.notNull(request.getContent(), "content");
        ValidationUtil.notNull(request.getIndex(), "index");

        listItemDao.findByIdValidated(userId, listItemId);

        List<Content> contents = contentDao.getByListItemIdAndType(userId, listItemId, ParentType.CHECKLIST_ITEM);
        ChecklistItem checklistItem = checklistItemFactory.create(userId, listItemId, false, request.getIndex());
        contents.add(contentFactory.create(userId, listItemId, ParentType.CHECKLIST_ITEM, checklistItem.getChecklistItemId(), request.getContent()));

        contentDao.save(checklistItem.getUserId(), checklistItem.getListItemId(), contents);
    }


    public void updateContent(UUID userId, UUID listItemId, UUID checklistItemId, String contentString) {
        ValidationUtil.notNull(contentString, "content");

        List<Content> contents = contentDao.getByListItemIdAndType(userId, listItemId, ParentType.CHECKLIST_ITEM);
        Content content = contents.stream()
            .filter(c -> c.getContent().containsKey(uuidConverter.convertDomain(checklistItemId)))
            .findAny()
            .orElseThrow(() -> ExceptionFactory.notFound("Content not found for checklistItemId " + checklistItemId));

        content.add(uuidConverter.convertDomain(checklistItemId), contentString);

        contentDao.save(userId, listItemId, contents);
    }

    public void deleteChecklistItem(UUID userId, UUID listItemId, UUID checklistItemId) {
        listItemDao.findByIdValidated(userId, listItemId);

        checklistItemDao.delete(userId, listItemId, checklistItemId);
        contentDao.delete(userId, listItemId, checklistItemId, ParentType.CHECKLIST_ITEM);
    }

    public void updateStatus(UUID userId, UUID listItemId, UUID checklistItemId, Boolean status) {
        ValidationUtil.notNull(status, "status");

        ChecklistItem checklistItem = checklistItemDao.findByIdValidated(userId, listItemId, checklistItemId);

        checklistItem.setChecked(status);

        checklistItemDao.save(checklistItem);
    }

    public void deleteCheckedItems(UUID userId, UUID listItemId) {
        List<ChecklistItem> toDelete = checklistItemDao.getByListItemId(userId, listItemId)
            .stream()
            .filter(ChecklistItem::isChecked)
            .toList();

        checklistItemDao.delete(toDelete);
        contentDao.delete(
            userId,
            listItemId,
            toDelete.stream().map(ChecklistItem::getChecklistItemId).toList(),
            ParentType.CHECKLIST_ITEM
        );
    }
}