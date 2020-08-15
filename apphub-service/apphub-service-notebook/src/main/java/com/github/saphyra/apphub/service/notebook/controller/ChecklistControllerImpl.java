package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.request.ChecklistItemNodeRequest;
import com.github.saphyra.apphub.api.notebook.model.request.CreateChecklistItemRequest;
import com.github.saphyra.apphub.api.notebook.model.response.ChecklistResponse;
import com.github.saphyra.apphub.api.notebook.server.ChecklistController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.notebook.service.checklist.ChecklistItemQueryService;
import com.github.saphyra.apphub.service.notebook.service.checklist.ChecklistItemStatusUpdateService;
import com.github.saphyra.apphub.service.notebook.service.checklist.creation.ChecklistCreationService;
import com.github.saphyra.apphub.service.notebook.service.checklist.edition.EditChecklistItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ChecklistControllerImpl implements ChecklistController {
    private final ChecklistCreationService checklistCreationService;
    private final ChecklistItemQueryService checklistItemQueryService;
    private final EditChecklistItemService editChecklistItemService;
    private final ChecklistItemStatusUpdateService checklistItemStatusUpdateService;

    @Override
    public OneParamResponse<UUID> createChecklistItem(CreateChecklistItemRequest request, AccessTokenHeader accessTokenHeader) {
        log.info("Creating new checklist for user {}", accessTokenHeader.getUserId());
        return new OneParamResponse<>(checklistCreationService.create(request, accessTokenHeader.getUserId()));
    }

    @Override
    public void editChecklistItem(List<ChecklistItemNodeRequest> request, UUID listItemId) {
        log.info("Editing checklistItem with listItemId {}", listItemId);
        editChecklistItemService.edit(request, listItemId);
    }

    @Override
    public ChecklistResponse getChecklistItem(UUID listItemId) {
        log.info("Querying checklist item with id {}", listItemId);
        return checklistItemQueryService.query(listItemId);
    }

    @Override
    //TODO unit test
    //TODO api test
    //TODO unt test
    public void updateStatus(OneParamRequest<Boolean> request, UUID checklistItemId) {
        log.info("Updating status of {}", checklistItemId);
        checklistItemStatusUpdateService.update(checklistItemId, request.getValue());
    }
}
