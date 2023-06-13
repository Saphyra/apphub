package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.request.CustomTableRequest;
import com.github.saphyra.apphub.api.notebook.model.response.CustomTableCreatedResponse;
import com.github.saphyra.apphub.api.notebook.model.response.CustomTableResponse;
import com.github.saphyra.apphub.api.notebook.server.CustomTableController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.notebook.service.checklist_table.ChecklistTableStatusUpdateService;
import com.github.saphyra.apphub.service.notebook.service.custom_table.CheckedCustomTableItemDeletionService;
import com.github.saphyra.apphub.service.notebook.service.custom_table.CustomTableEditionService;
import com.github.saphyra.apphub.service.notebook.service.custom_table.creation.CustomTableCreationService;
import com.github.saphyra.apphub.service.notebook.service.custom_table.query.CustomTableQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class CustomTableControllerImpl implements CustomTableController {
    private final CustomTableCreationService customTableCreationService;
    private final CustomTableEditionService customTableEditionService;
    private final ChecklistTableStatusUpdateService checklistTableStatusUpdateService;
    private final CheckedCustomTableItemDeletionService checkedCustomTableItemDeletionService;
    private final CustomTableQueryService customTableQueryService;

    @Override
    public List<CustomTableCreatedResponse> createCustomTable(CustomTableRequest request, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to create a CustomTable", accessTokenHeader.getUserId());
        return customTableCreationService.create(accessTokenHeader.getUserId(), request);
    }

    @Override
    public List<CustomTableCreatedResponse> editCustomTable(CustomTableRequest request, UUID listItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to modify customTable {}", accessTokenHeader.getUserId(), listItemId);
        return customTableEditionService.editCustomTable(accessTokenHeader.getUserId(), listItemId, request);
    }

    @Override
    public CustomTableResponse getCustomTable(UUID listItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to query CustomTable {}", accessTokenHeader.getUserId(), listItemId);
        return customTableQueryService.getCustomTable(listItemId);
    }

    @Override
    public void setCustomTableRowStatus(UUID rowId, OneParamRequest<Boolean> status, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to modify row status of CustomTable row {}", accessTokenHeader.getUserId(), rowId);
        checklistTableStatusUpdateService.updateStatus(rowId, status.getValue());
    }

    @Override
    public void deleteCheckedItems(UUID listItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to delete checked items from CustomTable {}", accessTokenHeader.getUserId(), listItemId);
        checkedCustomTableItemDeletionService.deleteCheckedItems(listItemId);
    }
}
