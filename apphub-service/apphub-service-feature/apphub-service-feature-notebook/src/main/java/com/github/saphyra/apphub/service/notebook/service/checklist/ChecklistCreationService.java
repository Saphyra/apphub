package com.github.saphyra.apphub.service.notebook.service.checklist;

import com.github.saphyra.apphub.api.feature.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.feature.notebook.model.checklist.CreateChecklistRequest;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ChecklistItemFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.Content;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ContentFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ParentType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ChecklistCreationService {
    private final ChecklistValidator checklistValidator;
    private final ListItemFactory listItemFactory;
    private final ListItemDao listItemDao;
    private final ChecklistItemFactory checklistItemFactory;
    private final ContentFactory contentFactory;

    @Transactional
    public UUID create(UUID userId, CreateChecklistRequest request) {
        checklistValidator.validate(userId, request);

        ListItem listItem = listItemFactory.create(userId, request.getParent(), request.getTitle(), ListItemType.CHECKLIST);

        List<ChecklistItem> checklistItems = new ArrayList<>();
        List<Content> contents = new ArrayList<>();

        request.getItems()
                .forEach(item -> {
                    ChecklistItem checklistItem = checklistItemFactory.create(userId, listItem.getListItemId(), item.getChecked(), item.getIndex());
                    Content content = contentFactory.create(userId, listItem.getListItemId(), ParentType.CHECKLIST_ITEM, checklistItem.getChecklistItemId(), item.getContent());
                    contents.add(content);
                });

        listItemDao.saveChecklist(listItem, checklistItems, contents);

        return listItem.getListItemId();
    }
}
