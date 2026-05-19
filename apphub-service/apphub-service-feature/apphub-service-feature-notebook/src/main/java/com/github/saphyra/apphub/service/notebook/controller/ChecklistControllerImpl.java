package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.feature.notebook.model.checklist.AddChecklistItemRequest;
import com.github.saphyra.apphub.api.feature.notebook.model.checklist.ChecklistResponse;
import com.github.saphyra.apphub.api.feature.notebook.model.checklist.CreateChecklistRequest;
import com.github.saphyra.apphub.api.feature.notebook.model.checklist.EditChecklistRequest;
import com.github.saphyra.apphub.api.feature.notebook.server.ChecklistController;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.notebook.service.checklist.ChecklistItemCrudService;
import com.github.saphyra.apphub.service.notebook.service.checklist.OrderChecklistItemsService;
import com.github.saphyra.apphub.service.notebook.service.checklist.ChecklistCreationService;
import com.github.saphyra.apphub.service.notebook.service.checklist.EditChecklistService;
import com.github.saphyra.apphub.service.notebook.service.checklist.ChecklistQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChecklistControllerImpl implements ChecklistController {
    private final ChecklistCreationService checklistCreationService;
    private final ChecklistQueryService checklistQueryService;
    private final OrderChecklistItemsService orderChecklistItemsService;
    private final EditChecklistService editChecklistService;
    private final ChecklistItemCrudService checklistItemCrudService;

    @Override
    public OneParamResponse<UUID> createChecklist(CreateChecklistRequest request, AccessToken accessToken) {
        log.info("{} wants to create a new checklist.", accessToken.getUserId());
        UUID listItemId = checklistCreationService.create(accessToken.getUserId(), request);
        return new OneParamResponse<>(listItemId);
    }

    @Override
    public ChecklistResponse editChecklist(EditChecklistRequest request, UUID listItemId, AccessToken accessToken) {
        log.info("{} wants to edit checklist {}", accessToken.getUserId(), listItemId);
        return editChecklistService.edit(accessToken.getUserId(), listItemId, request);
    }

    @Override
    public ChecklistResponse getChecklist(UUID listItemId, AccessToken accessToken) {
        log.info("{} wants to query checklist {}", accessToken.getUserId(), listItemId);
        return checklistQueryService.getChecklistResponse(accessToken.getUserId(), listItemId);
    }

    @Override
    public void updateStatus(OneParamRequest<Boolean> request,UUID listItemId, UUID checklistItemId, AccessToken accessToken) {
        log.info("{} wants to change status of checklistItem {}", accessToken.getUserId(), checklistItemId);
        checklistItemCrudService.updateStatus(accessToken.getUserId(), listItemId, checklistItemId, request.getValue());
    }

    @Override
    public void deleteChecklistItem(UUID listItemId, UUID checklistItemId, AccessToken accessToken) {
        log.info("{} wants to delete checklistItem {} of ListItem {}", accessToken.getUserId(), checklistItemId, listItemId);
        checklistItemCrudService.deleteChecklistItem(accessToken.getUserId(), listItemId, checklistItemId);
    }

    @Override
    public ChecklistResponse deleteCheckedItems(UUID listItemId, AccessToken accessToken) {
        log.info("{} wants to delete checked items of checklist {}", accessToken.getUserId(), listItemId);

        checklistItemCrudService.deleteCheckedItems(accessToken.getUserId(), listItemId);

        return getChecklist(listItemId, accessToken);
    }

    @Override
    public ChecklistResponse orderItems(UUID listItemId, AccessToken accessToken) {
        log.info("{} wants to order items of checklist {}", accessToken.getUserId(), listItemId);
        return orderChecklistItemsService.orderItems(accessToken.getUserId(), listItemId);
    }

    @Override
    public void editChecklistItem(OneParamRequest<String> content, UUID listItemId, UUID checklistItemId, AccessToken accessToken) {
        log.info("{} wants to modify checklist item {} of listItem {}", accessToken.getUserId(), checklistItemId, listItemId);
        checklistItemCrudService.updateContent(accessToken.getUserId(), listItemId, checklistItemId, content.getValue());
    }

    @Override
    public ChecklistResponse addChecklistItem(AddChecklistItemRequest request, UUID listItemId, AccessToken accessToken) {
        log.info("{} wants to add new item to checklist {}", accessToken.getUserId(), listItemId);

        checklistItemCrudService.addChecklistItem(accessToken.getUserId(), listItemId, request);

        return getChecklist(listItemId, accessToken);
    }
}
