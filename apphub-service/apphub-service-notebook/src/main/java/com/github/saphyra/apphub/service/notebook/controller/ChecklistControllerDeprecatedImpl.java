package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.request.CreateChecklistRequestDeprecated;
import com.github.saphyra.apphub.api.notebook.model.request.EditChecklistItemRequest;
import com.github.saphyra.apphub.api.notebook.model.response.ChecklistResponse;
import com.github.saphyra.apphub.api.notebook.server.ChecklistControllerDeprecated;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.notebook.service.checklist_deprecated.ChecklistItemDeletionService;
import com.github.saphyra.apphub.service.notebook.service.checklist_deprecated.ChecklistItemQueryService;
import com.github.saphyra.apphub.service.notebook.service.checklist_deprecated.ChecklistItemStatusUpdateService;
import com.github.saphyra.apphub.service.notebook.service.checklist_deprecated.ChecklistItemsOrderService;
import com.github.saphyra.apphub.service.notebook.service.checklist_deprecated.creation.ChecklistCreationService;
import com.github.saphyra.apphub.service.notebook.service.checklist_deprecated.edition.EditChecklistItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class ChecklistControllerDeprecatedImpl implements ChecklistControllerDeprecated {
    private final ChecklistCreationService checklistCreationService;
    private final ChecklistItemQueryService checklistItemQueryService;
    private final EditChecklistItemService editChecklistItemService;
    private final ChecklistItemStatusUpdateService checklistItemStatusUpdateService;
    private final ChecklistItemDeletionService checklistItemDeletionService;
    private final ChecklistItemsOrderService checklistItemsOrderService;

    @Override
    public OneParamResponse<UUID> createChecklist(CreateChecklistRequestDeprecated request, AccessTokenHeader accessTokenHeader) {
        log.info("Creating new checklist for user {}", accessTokenHeader.getUserId());
        return new OneParamResponse<>(checklistCreationService.create(request, accessTokenHeader.getUserId()));
    }

    @Override
    public ChecklistResponse editChecklist(EditChecklistItemRequest request, UUID listItemId) {
        log.info("Editing checklistItem with listItemId {}", listItemId);
        editChecklistItemService.edit(request, listItemId);
        return getChecklist(listItemId);
    }

    @Override
    public ChecklistResponse getChecklist(UUID listItemId) {
        log.info("Querying checklist item with id {}", listItemId);
        return checklistItemQueryService.query(listItemId);
    }

    @Override
    public void updateStatus(OneParamRequest<Boolean> request, UUID checklistItemId) {
        log.info("Updating status of {}", checklistItemId);
        checklistItemStatusUpdateService.update(checklistItemId, request.getValue());
    }

    @Override
    public void deleteChecklistItem(UUID checklistItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to delete checklistItem {}", accessTokenHeader.getUserId(), checklistItemId);
        checklistItemDeletionService.delete(checklistItemId);
    }

    @Override
    public ChecklistResponse deleteCheckedItems(UUID listItemId) {
        log.info("Deleting checked items of checklist {}", listItemId);
        checklistItemDeletionService.deleteCheckedItems(listItemId);
        return getChecklist(listItemId);
    }

    @Override
    public ChecklistResponse orderItems(UUID listItemId) {
        log.info("Ordering checklist items {}", listItemId);
        checklistItemsOrderService.orderChecklistItems(listItemId);
        return getChecklist(listItemId);
    }
}
