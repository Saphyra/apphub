package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.request.CreateTableRequest;
import com.github.saphyra.apphub.api.notebook.model.request.EditTableRequest;
import com.github.saphyra.apphub.api.notebook.model.response.TableResponse;
import com.github.saphyra.apphub.api.notebook.server.TableController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemType;
import com.github.saphyra.apphub.service.notebook.service.ConvertTableToChecklistTableService;
import com.github.saphyra.apphub.service.notebook.service.table.TableQueryService;
import com.github.saphyra.apphub.service.notebook.service.table.creation.TableCreationService;
import com.github.saphyra.apphub.service.notebook.service.table.edition.TableEditionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
public class TableControllerImpl implements TableController {
    private final TableCreationService tableCreationService;
    private final TableEditionService tableEditionService;
    private final TableQueryService tableQueryService;
    private final ConvertTableToChecklistTableService convertTableToChecklistTableService;

    @Override
    public OneParamResponse<UUID> createTable(CreateTableRequest request, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to create a table.", accessTokenHeader.getUserId());
        return new OneParamResponse<>(tableCreationService.create(request, accessTokenHeader.getUserId(), ListItemType.TABLE));
    }

    @Override
    public void editTable(EditTableRequest request, UUID listItemId) {
        log.info("Editing table {}", listItemId);
        tableEditionService.edit(listItemId, request);
    }

    @Override
    public TableResponse getTable(UUID listItemId) {
        log.info("Querying table with listItemId {}", listItemId);
        return tableQueryService.getTable(listItemId);
    }

    @Override
    public void convertToChecklistTable(UUID listItemId) {
        log.info("Converting table {} to checklistTable", listItemId);
        convertTableToChecklistTableService.convert(listItemId);
    }
}
