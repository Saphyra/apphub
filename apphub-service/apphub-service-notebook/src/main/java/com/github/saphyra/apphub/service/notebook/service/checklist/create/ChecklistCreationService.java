package com.github.saphyra.apphub.service.notebook.service.checklist.create;

import com.github.saphyra.apphub.api.notebook.model.checklist.CreateChecklistRequest;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.api.notebook.model.ListItemType;
import com.github.saphyra.apphub.service.notebook.service.ListItemFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO uni test
public class ChecklistCreationService {
    private final CreateChecklistRequestValidator createChecklistRequestValidator;
    private final ListItemFactory listItemFactory;
    private final ListItemDao listItemDao;
    private final ChecklistItemCreationService checklistItemCreationService;

    @Transactional
    public void create(UUID userId, CreateChecklistRequest request) {
        createChecklistRequestValidator.validate(request);

        ListItem listItem = listItemFactory.create(userId, request.getTitle(), request.getParent(), ListItemType.CHECKLIST);
        listItemDao.save(listItem);

        checklistItemCreationService.create(userId, listItem.getListItemId(), request.getItems());
    }
}
