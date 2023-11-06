package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.checklist.AddChecklistItemRequest;
import com.github.saphyra.apphub.api.notebook.model.checklist.ChecklistResponse;
import com.github.saphyra.apphub.api.notebook.model.checklist.CreateChecklistRequest;
import com.github.saphyra.apphub.api.notebook.model.checklist.EditChecklistRequest;
import com.github.saphyra.apphub.api.notebook.server.ChecklistController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
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
    public OneParamResponse<UUID> createChecklist(CreateChecklistRequest request, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to create a new checklist.", accessTokenHeader.getUserId());
        UUID listItemId = checklistCreationService.create(accessTokenHeader.getUserId(), request);
        return new OneParamResponse<>(listItemId);
    }

    @Override
    public ChecklistResponse editChecklist(EditChecklistRequest request, UUID listItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to edit checklist {}", accessTokenHeader.getUserId(), listItemId);
        return editChecklistService.edit(accessTokenHeader.getUserId(), listItemId, request);
    }

    @Override
    public ChecklistResponse getChecklist(UUID listItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to query checklist {}", accessTokenHeader.getUserId(), listItemId);
        return checklistQueryService.getChecklistResponse(listItemId);
    }

    @Override
    public void updateStatus(OneParamRequest<Boolean> request, UUID checklistItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to change status of checklistItem {}", accessTokenHeader.getUserId(), checklistItemId);
        checklistItemStatusUpdateService.updateStatus(checklistItemId, request.getValue());
    }

    @Override
    public void deleteChecklistItem(UUID checklistItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to delete checklistItem {}", accessTokenHeader.getUserId(), checklistItemId);
        checklistItemDeletionService.deleteChecklistItem(checklistItemId);
    }

    @Override
    public ChecklistResponse deleteCheckedItems(UUID listItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to delete checked items of checklist {}", accessTokenHeader.getUserId(), listItemId);
        return deleteCheckedItemsOfChecklistService.deleteCheckedItems(listItemId);
    }

    @Override
    public ChecklistResponse orderItems(UUID listItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to order items of checklist {}", accessTokenHeader.getUserId(), listItemId);
        return orderChecklistItemsService.orderItems(listItemId);
    }

    @Override
    public void editChecklistItem(OneParamRequest<String> content, UUID checklistItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to modify checklist item {}", accessTokenHeader.getUserId(), checklistItemId);
        checklistItemContentUpdateService.updateContent(checklistItemId, content.getValue());
    }

    @Override
    public ChecklistResponse addChecklistItem(AddChecklistItemRequest request, UUID listItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to add new item to checklist {}", accessTokenHeader.getUserId(), listItemId);

        checklistItemAdditionService.addChecklistItem(accessTokenHeader.getUserId(), listItemId, request);

        return getChecklist(listItemId, accessTokenHeader);
    }
}
