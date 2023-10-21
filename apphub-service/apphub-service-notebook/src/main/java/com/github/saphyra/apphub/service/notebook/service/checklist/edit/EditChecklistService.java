package com.github.saphyra.apphub.service.notebook.service.checklist.edit;

import com.github.saphyra.apphub.api.notebook.model.checklist.ChecklistResponse;
import com.github.saphyra.apphub.api.notebook.model.checklist.EditChecklistRequest;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.service.checklist.query.ChecklistQueryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class EditChecklistService {
    private final EditChecklistRequestValidator editChecklistRequestValidator;
    private final ChecklistQueryService checklistQueryService;
    private final EditChecklistRowDeleter editChecklistRowDeleter;
    private final EditChecklistRowSaver editChecklistRowSaver;
    private final ListItemDao listItemDao;

    @Transactional
    public ChecklistResponse edit(UUID userId, UUID listItemId, EditChecklistRequest request) {
        editChecklistRequestValidator.validate(request);

        ListItem listItem = listItemDao.findByIdValidated(listItemId);
        listItem.setTitle(request.getTitle());
        listItemDao.save(listItem);

        editChecklistRowDeleter.deleteRemovedItems(listItemId, request.getItems());
        editChecklistRowSaver.saveItems(userId, listItemId, request.getItems());

        return checklistQueryService.getChecklistResponse(listItemId);
    }
}
