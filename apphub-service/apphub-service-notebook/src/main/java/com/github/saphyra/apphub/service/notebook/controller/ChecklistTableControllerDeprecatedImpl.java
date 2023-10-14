package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.request.CreateChecklistTableRequest;
import com.github.saphyra.apphub.api.notebook.model.request.EditChecklistTableRequest;
import com.github.saphyra.apphub.api.notebook.model.response.ChecklistTableResponse;
import com.github.saphyra.apphub.api.notebook.server.ChecklistTableControllerDeprecated;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.notebook.service.checklist_table_deprecated.CheckedChecklistTableItemDeletionService;
import com.github.saphyra.apphub.service.notebook.service.checklist_table_deprecated.ChecklistTableQueryService;
import com.github.saphyra.apphub.service.notebook.service.checklist_table_deprecated.ChecklistTableStatusUpdateService;
import com.github.saphyra.apphub.service.notebook.service.checklist_table_deprecated.creation.ChecklistTableCreationService;
import com.github.saphyra.apphub.service.notebook.service.checklist_table_deprecated.edition.ChecklistTableEditionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class ChecklistTableControllerDeprecatedImpl implements ChecklistTableControllerDeprecated {
    private final ChecklistTableCreationService checklistTableCreationService;
    private final ChecklistTableEditionService checklistTableEditionService;
    private final ChecklistTableQueryService checklistTableQueryService;
    private final ChecklistTableStatusUpdateService checklistTableStatusUpdateService;
    private final CheckedChecklistTableItemDeletionService checkedChecklistTableItemDeletionService;

    @Override
    public OneParamResponse<UUID> createChecklistTable(CreateChecklistTableRequest request, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to create a checklistTable", accessTokenHeader.getUserId());
        log.debug("CreateChecklistTableRequest: {}", request);
        return new OneParamResponse<>(checklistTableCreationService.create(accessTokenHeader.getUserId(), request));
    }

    @Override
    public ChecklistTableResponse editChecklistTable(EditChecklistTableRequest request, UUID listItemId) {
        log.info("Editing checklistTable {}", listItemId);
        log.debug("EditChecklistTableRequest: {}", request);
        checklistTableEditionService.edit(listItemId, request);

        return getChecklistTable(listItemId);
    }

    @Override
    public ChecklistTableResponse getChecklistTable(UUID listItemId) {
        log.info("Querying checklistTable {}", listItemId);
        return checklistTableQueryService.getChecklistTable(listItemId);
    }

    @Override
    public void setChecklistTableRowStatus(UUID rowId, OneParamRequest<Boolean> status) {
        log.info("Setting checklistTable row status for row {}", rowId);
        checklistTableStatusUpdateService.updateStatus( rowId, status.getValue());
    }

    @Override
    public ChecklistTableResponse deleteCheckedItems(UUID listItemId) {
        log.info("Deleting checked items of checklistTable {}", listItemId);
        checkedChecklistTableItemDeletionService.deleteCheckedItems(listItemId);
        return getChecklistTable(listItemId);
    }
}
