package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.feature.notebook.model.checklist.AddChecklistItemRequest;
import com.github.saphyra.apphub.api.feature.notebook.model.checklist.ChecklistResponse;
import com.github.saphyra.apphub.api.feature.notebook.model.checklist.CreateChecklistRequest;
import com.github.saphyra.apphub.api.feature.notebook.model.checklist.EditChecklistRequest;
import com.github.saphyra.apphub.api.feature.notebook.server.ChecklistController;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.notebook.service.checklist.ChecklistItemAdditionService;
import com.github.saphyra.apphub.service.notebook.service.checklist.ChecklistItemContentUpdateService;
import com.github.saphyra.apphub.service.notebook.service.checklist.ChecklistItemDeletionService;
import com.github.saphyra.apphub.service.notebook.service.checklist.ChecklistItemStatusUpdateService;
import com.github.saphyra.apphub.service.notebook.service.checklist.DeleteCheckedItemsOfChecklistService;
import com.github.saphyra.apphub.service.notebook.service.checklist.OrderChecklistItemsService;
import com.github.saphyra.apphub.service.notebook.service.checklist.create.ChecklistCreationService;
import com.github.saphyra.apphub.service.notebook.service.checklist.edit.EditChecklistService;
import com.github.saphyra.apphub.service.notebook.service.checklist.query.ChecklistQueryService;
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
    private final ChecklistItemStatusUpdateService checklistItemStatusUpdateService;
    private final ChecklistItemDeletionService checklistItemDeletionService;
    private final DeleteCheckedItemsOfChecklistService deleteCheckedItemsOfChecklistService;
    private final OrderChecklistItemsService orderChecklistItemsService;
    private final EditChecklistService editChecklistService;
    private final ChecklistItemContentUpdateService checklistItemContentUpdateService;
    private final ChecklistItemAdditionService checklistItemAdditionService;

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
        return checklistQueryService.getChecklistResponse(listItemId);
    }

    @Override
    public void updateStatus(OneParamRequest<Boolean> request, UUID checklistItemId, AccessToken accessToken) {
        log.info("{} wants to change status of checklistItem {}", accessToken.getUserId(), checklistItemId);
        checklistItemStatusUpdateService.updateStatus(checklistItemId, request.getValue());
    }

    @Override
    public void deleteChecklistItem(UUID checklistItemId, AccessToken accessToken) {
        log.info("{} wants to delete checklistItem {}", accessToken.getUserId(), checklistItemId);
        checklistItemDeletionService.deleteChecklistItem(checklistItemId);
    }

    @Override
    public ChecklistResponse deleteCheckedItems(UUID listItemId, AccessToken accessToken) {
        log.info("{} wants to delete checked items of checklist {}", accessToken.getUserId(), listItemId);
        return deleteCheckedItemsOfChecklistService.deleteCheckedItems(listItemId);
    }

    @Override
    public ChecklistResponse orderItems(UUID listItemId, AccessToken accessToken) {
        log.info("{} wants to order items of checklist {}", accessToken.getUserId(), listItemId);
        return orderChecklistItemsService.orderItems(listItemId);
    }

    @Override
    public void editChecklistItem(OneParamRequest<String> content, UUID checklistItemId, AccessToken accessToken) {
        log.info("{} wants to modify checklist item {}", accessToken.getUserId(), checklistItemId);
        checklistItemContentUpdateService.updateContent(checklistItemId, content.getValue());
    }

    @Override
    public ChecklistResponse addChecklistItem(AddChecklistItemRequest request, UUID listItemId, AccessToken accessToken) {
        log.info("{} wants to add new item to checklist {}", accessToken.getUserId(), listItemId);

        checklistItemAdditionService.addChecklistItem(accessToken.getUserId(), listItemId, request);

        return getChecklist(listItemId, accessToken);
    }
}
