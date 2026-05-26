package com.github.saphyra.apphub.service.notebook.service.checklist;

import com.github.saphyra.apphub.api.feature.notebook.model.checklist.AddChecklistItemRequest;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item.ChecklistItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item.ChecklistItemFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChecklistItemCrudService {
    private final ListItemDao listItemDao;
    private final ChecklistItemFactory checklistItemFactory;
    private final ContentFactory contentFactory;
    private final ChecklistItemDao checklistItemDao;
    private final ContentDao contentDao;

    public void addChecklistItem(UUID userId, UUID listItemId, AddChecklistItemRequest request) {
        ValidationUtil.notNull(request.getContent(), "content");
        ValidationUtil.notNull(request.getIndex(), "index");

        listItemDao.findByIdValidated(userId, listItemId);

        List<Content> contents = new ArrayList<>(contentDao.getByListItemId(listItemId));
        ChecklistItem checklistItem = checklistItemFactory.create(listItemId, false, request.getIndex());
        contents.add(contentFactory.create(listItemId, checklistItem.getChecklistItemId(), request.getContent()));

        contentDao.save(checklistItem.getListItemId(), contents);
        checklistItemDao.save(checklistItem);
    }

    public void updateContent(UUID listItemId, UUID checklistItemId, String contentString) {
        ValidationUtil.notNull(contentString, "content");

        List<Content> contents = contentDao.getByListItemId(listItemId);
        Content content = contents.stream()
            .filter(c -> c.contains(checklistItemId))
            .findAny()
            .orElseThrow(() -> ExceptionFactory.notFound("Content not found for checklistItemId " + checklistItemId));

        content.add(checklistItemId, contentString);

        contentDao.save(listItemId, contents);
    }

    public void deleteChecklistItem(UUID userId, UUID listItemId, UUID checklistItemId) {
        listItemDao.findByIdValidated(userId, listItemId);

        checklistItemDao.delete(listItemId, checklistItemId);
        contentDao.delete(listItemId, checklistItemId);
    }

    public void updateStatus(UUID listItemId, UUID checklistItemId, Boolean status) {
        ValidationUtil.notNull(status, "status");

        ChecklistItem checklistItem = checklistItemDao.findByIdValidated(listItemId, checklistItemId);

        checklistItem.setChecked(status);

        checklistItemDao.save(checklistItem);
    }

    public void deleteCheckedItems(UUID listItemId) {
        List<ChecklistItem> toDelete = checklistItemDao.getByListItemId(listItemId)
            .stream()
            .filter(ChecklistItem::isChecked)
            .toList();

        checklistItemDao.delete(toDelete);
        contentDao.deleteKeys(
            listItemId,
            toDelete.stream().map(ChecklistItem::getChecklistItemId).toList()
        );
    }
}