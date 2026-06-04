package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.feature.notebook.model.table.CreateTableRequest;
import com.github.saphyra.apphub.api.feature.notebook.model.table.EditTableRequest;
import com.github.saphyra.apphub.api.feature.notebook.model.table.EditTableResponse;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableResponse;
import com.github.saphyra.apphub.api.feature.notebook.server.TableController;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.notebook.service.table.CheckboxColumnStatusUpdateService;
import com.github.saphyra.apphub.service.notebook.service.table.CheckedTableRowDeletionService;
import com.github.saphyra.apphub.service.notebook.service.table.TableCreationService;
import com.github.saphyra.apphub.service.notebook.service.table.edit.TableEditionService;
import com.github.saphyra.apphub.service.notebook.service.table.TableQueryService;
import com.github.saphyra.apphub.service.notebook.service.table.TableRowStatusUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
class TableControllerImpl implements TableController {
    private final TableCreationService tableCreationService;
    private final TableQueryService tableQueryService;
    private final TableRowStatusUpdateService tableRowStatusUpdateService;
    private final CheckedTableRowDeletionService checkedTableRowDeletionService;
    private final TableEditionService tableEditionService;
    private final CheckboxColumnStatusUpdateService checkboxColumnStatusUpdateService;

    @Override
    public List<TableFileUploadResponse> createTable(CreateTableRequest request, AccessToken accessToken) {
        log.info("{} wants to create a {}", accessToken.getUserId(), request.getListItemType());
        List<TableFileUploadResponse> tableFileUploadResponses = tableCreationService.create(accessToken.getUserId(), request);
        log.info("{}", tableFileUploadResponses);
        return tableFileUploadResponses;
    }

    @Override
    public EditTableResponse editTable(EditTableRequest request, UUID listItemId, AccessToken accessToken) {
        log.info("{} wants to edit table {}", accessToken.getUserId(), listItemId);
        List<TableFileUploadResponse> fileUploads = tableEditionService.editTable(accessToken.getUserId(), listItemId, request);
        return EditTableResponse.builder()
            .tableResponse(getTable(listItemId, accessToken))
            .fileUpload(fileUploads)
            .build();
    }

    @Override
    public TableResponse getTable(UUID listItemId, AccessToken accessToken) {
        log.info("{} wants to query table {}", accessToken.getUserId(), listItemId);
        return tableQueryService.getTable(accessToken.getUserId(), listItemId);
    }

    @Override
    public void setRowStatus(UUID listItemId, UUID rowId, OneParamRequest<Boolean> status, AccessToken accessToken) {
        log.info("{} wants to modify status of table row {}", accessToken.getUserId(), rowId);
        tableRowStatusUpdateService.setRowStatus(listItemId, rowId, status.getValue());
    }

    @Override
    public TableResponse deleteCheckedRows(UUID listItemId, AccessToken accessToken) {
        log.info("{} wants to delete checked rows of table {}", accessToken.getUserId(), listItemId);
        checkedTableRowDeletionService.deleteCheckedRows(accessToken.getUserId(), listItemId);
        return getTable(listItemId, accessToken);
    }

    @Override
    public void setCheckboxColumnStatus(UUID listItemId, UUID rowId, UUID columnId, OneParamRequest<Boolean> status, AccessToken accessToken) {
        log.info("{} wants to change the status of checked column {}", accessToken.getUserId(), columnId);
        checkboxColumnStatusUpdateService.updateColumnStatus(listItemId, rowId, columnId, status.getValue());
    }
}
