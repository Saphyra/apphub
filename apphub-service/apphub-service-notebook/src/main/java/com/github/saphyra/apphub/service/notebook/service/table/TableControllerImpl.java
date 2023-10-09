package com.github.saphyra.apphub.service.notebook.service.table;

import com.github.saphyra.apphub.api.notebook.model.table.CreateTableRequest;
import com.github.saphyra.apphub.api.notebook.model.table.EditTableRequest;
import com.github.saphyra.apphub.api.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.api.notebook.model.table.TableResponse;
import com.github.saphyra.apphub.api.notebook.server.TableController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class TableControllerImpl implements TableController {
    @Override
    public List<TableFileUploadResponse> createTable(CreateTableRequest request, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to create a {}", accessTokenHeader.getUserId(), request.getListItemType());
        return null;
    }

    @Override
    public List<TableFileUploadResponse> editTable(EditTableRequest request, UUID listItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to edit table {}", accessTokenHeader.getUserId(), listItemId);
        return null;
    }

    @Override
    public TableResponse getTable(UUID listItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to query table {}", accessTokenHeader.getUserId(), listItemId);
        return null;
    }

    @Override
    public void setRowStatus(UUID rowId, OneParamRequest<Boolean> status, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to modify status of table row {}", accessTokenHeader.getUserId(), rowId);
    }

    @Override
    public void deleteCheckedRows(UUID listItemId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to delete checked rows of table {}", accessTokenHeader.getUserId(), listItemId);
    }
}
