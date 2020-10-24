package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.request.CreateChecklistTableRequest;
import com.github.saphyra.apphub.api.notebook.model.request.EditChecklistTableRequest;
import com.github.saphyra.apphub.api.notebook.model.response.ChecklistTableResponse;
import com.github.saphyra.apphub.api.notebook.server.ChecklistTableController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.notebook.service.checklist_table.ChecklistTableQueryService;
import com.github.saphyra.apphub.service.notebook.service.checklist_table.ChecklistTableStatusUpdateService;
import com.github.saphyra.apphub.service.notebook.service.checklist_table.creation.ChecklistTableCreationService;
import com.github.saphyra.apphub.service.notebook.service.checklist_table.edition.ChecklistTableEditionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ChecklistTableControllerImpl implements ChecklistTableController {
    private final ChecklistTableCreationService checklistTableCreationService;
    private final ChecklistTableEditionService checklistTableEditionService;
    private final ChecklistTableQueryService checklistTableQueryService;
    private final ChecklistTableStatusUpdateService checklistTableStatusUpdateService;

    @Override
    //TODO unit test
    //TODO int test
    //TODO API test
    public OneParamResponse<UUID> createChecklistTable(CreateChecklistTableRequest request, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to create a checklistTable", accessTokenHeader.getUserId());
        return new OneParamResponse<>(checklistTableCreationService.create(accessTokenHeader.getUserId(), request));
    }

    @Override
    //TODO unit test
    //TODO int test
    //TODO API test
    public void editChecklistTable(EditChecklistTableRequest request, UUID listItemId) {
        log.info("Editing checklistTable {}", listItemId);
        checklistTableEditionService.edit(listItemId, request);
    }

    @Override
    //TODO unit test
    //TODO int test
    //TODO API test
    public ChecklistTableResponse getChecklistTable(UUID listItemId) {
        log.info("Querying checklistTable {}", listItemId);
        return checklistTableQueryService.getChecklistTable(listItemId);
    }

    @Override
    //TODO unit test
    //TODO int test
    //TODO API test
    public void setChecklistTableRowStatus(UUID listItemId, Integer rowIndex, OneParamRequest<Boolean> status) {
        log.info("Setting checklistTable row status for table {}", listItemId);
        checklistTableStatusUpdateService.updateStatus(listItemId, rowIndex, status.getValue());
    }
}
