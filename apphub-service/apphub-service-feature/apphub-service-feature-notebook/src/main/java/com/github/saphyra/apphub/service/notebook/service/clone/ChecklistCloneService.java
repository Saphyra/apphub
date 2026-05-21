package com.github.saphyra.apphub.service.notebook.service.clone;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item.ChecklistItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.checklist_item.ChecklistItemFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.CommonListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ContentFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.content.ParentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class ChecklistCloneService {
    private final ListItemFactory listItemFactory;
    private final ChecklistItemFactory checklistItemFactory;
    private final UuidConverter uuidConverter;
    private final ContentFactory contentFactory;
    private final CommonListItemDao commonListItemDao;
    private final ContentDao contentDao;
    private final ChecklistItemDao checklistItemDao;

    void clone(UUID parent, ListItem toClone) {
        ListItem clone = listItemFactory.clone(parent, toClone);
        List<ChecklistItem> items = checklistItemDao.getByListItemId(toClone.getUserId(), toClone.getListItemId());
        List<Content> contents = contentDao.getByListItemIdAndType(toClone.getUserId(), toClone.getListItemId(), ParentType.CHECKLIST_ITEM);
        List<Content> clonedContents = new ArrayList<>();
        List<ChecklistItem> clonedItems = items.stream()
            .map(item -> {
                ChecklistItem clonedItem = checklistItemFactory.clone(clone.getListItemId(), item);

                String key = uuidConverter.convertDomain(item.getChecklistItemId());
                Content content = contents.stream()
                    .filter(c -> c.contains(key))
                    .findAny()
                    .orElseThrow(() -> ExceptionFactory.loggedException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.DATA_NOT_FOUND, "Content not found by checklistItemId " + item.getChecklistItemId()));

                Content clonedContent = contentFactory.create(clone.getUserId(), clone.getListItemId(), ParentType.CHECKLIST_ITEM, clonedItem.getChecklistItemId(), content.get(key));
                clonedContents.add(clonedContent);

                return clonedItem;
            })
            .toList();
        commonListItemDao.saveChecklist(clone, clonedItems, clonedContents);
    }
}
