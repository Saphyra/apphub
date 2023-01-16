package com.github.saphyra.apphub.service.notebook.service.checklist.creation;

import com.github.saphyra.apphub.api.notebook.model.request.CreateChecklistItemRequest;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItemDao;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemType;
import com.github.saphyra.apphub.service.notebook.service.ListItemFactory;
import com.github.saphyra.apphub.service.notebook.service.checklist.ChecklistItemFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChecklistCreationService {
    private final ChecklistItemFactory checklistItemFactory;
    private final CreateChecklistItemRequestValidator createChecklistItemRequestValidator;
    private final ListItemFactory listItemFactory;
    private final ListItemDao listItemDao;
    private final ContentDao contentDao;
    private final ChecklistItemDao checklistItemDao;

    @Transactional
    public UUID create(CreateChecklistItemRequest request, UUID userId) {
        createChecklistItemRequestValidator.validate(request);

        ListItem listItem = listItemFactory.create(userId, request.getTitle(), request.getParent(), ListItemType.CHECKLIST);

        listItemDao.save(listItem);

        request.getNodes()
            .stream()
            .map(nodeRequest -> checklistItemFactory.create(listItem, nodeRequest))
            .forEach(nodeContentWrapper -> {
                contentDao.save(nodeContentWrapper.getEntity2());
                checklistItemDao.save(nodeContentWrapper.getEntity1());
            });


        return listItem.getListItemId();
    }
}
