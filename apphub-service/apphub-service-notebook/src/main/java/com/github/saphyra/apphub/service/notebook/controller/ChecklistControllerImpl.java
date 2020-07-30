package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.request.ChecklistItemNodeRequest;
import com.github.saphyra.apphub.api.notebook.model.request.CreateChecklistItemRequest;
import com.github.saphyra.apphub.api.notebook.server.ChecklistController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.notebook.service.checklist.creation.ChecklistCreationService;
import com.github.saphyra.apphub.service.notebook.service.checklist.edit.EditChecklistItemService;
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
    private final EditChecklistItemService editChecklistItemService;

    @Override
    //TODO unit test
    //TODO api test
    //TODO int test
    public OneParamResponse<UUID> createChecklistItem(CreateChecklistItemRequest request, AccessTokenHeader accessTokenHeader) {
        log.info("Creating new checklist for user {}", accessTokenHeader.getUserId());
        return new OneParamResponse<>(checklistCreationService.create(request, accessTokenHeader.getUserId()));
    }

    @Override
    //TODO unit test
    //TODO api test
    //TODO int test
    public void editChecklistItem(List<ChecklistItemNodeRequest> request, UUID listItemId) {
        log.info("Editing checklistItem with listItemId {}", listItemId);
        editChecklistItemService.edit(request, listItemId);
    }
}
