package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.table.CreateTableRequest;
import com.github.saphyra.apphub.api.notebook.model.table.EditTableRequest;
import com.github.saphyra.apphub.api.notebook.model.table.EditTableResponse;
import com.github.saphyra.apphub.api.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.api.notebook.model.table.TableResponse;
import com.github.saphyra.apphub.api.notebook.server.TableController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.notebook.service.table.CheckedTableRowDeletionService;
import com.github.saphyra.apphub.service.notebook.service.table.query.TableQueryService;
import com.github.saphyra.apphub.service.notebook.service.table.TableRowStatusUpdateService;
import com.github.saphyra.apphub.service.notebook.service.table.creation.TableCreationService;
import com.github.saphyra.apphub.service.notebook.service.table.edit.TableEditionService;
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

    @Override
    public List<TableFileUploadResponse> createTable(CreateTableRequest request, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to create a {}", accessTokenHeader.getUserId(), request.getListItemType());
        List<TableFileUploadResponse> tableFileUploadResponses = tableCreationService.create(accessTokenHeader.getUserId(), request);
        log.info("{}", tableFileUploadResponses);
        return tableFileUploadResponses;
    }

    @Override
    public EditTableResponse editTable(EditTableRequest request, UUID listItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to edit table {}", accessTokenHeader.getUserId(), listItemId);
        return tableEditionService.editTable(listItemId, request);
    }

    @Override
    public TableResponse getTable(UUID listItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to query table {}", accessTokenHeader.getUserId(), listItemId);
        return tableQueryService.getTable(listItemId);
    }

    @Override
    public void setRowStatus(UUID rowId, OneParamRequest<Boolean> status, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to modify status of table row {}", accessTokenHeader.getUserId(), rowId);
        tableRowStatusUpdateService.setRowStatus(rowId, status.getValue());
    }

    @Override
    public TableResponse deleteCheckedRows(UUID listItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to delete checked rows of table {}", accessTokenHeader.getUserId(), listItemId);
        checkedTableRowDeletionService.deleteCheckedRows(listItemId);
        return getTable(listItemId, accessTokenHeader);
    }
}
