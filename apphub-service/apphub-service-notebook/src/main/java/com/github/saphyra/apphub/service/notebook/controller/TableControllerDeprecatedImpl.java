package com.github.saphyra.apphub.service.notebook.controller;

import com.github.saphyra.apphub.api.notebook.model.request.CreateTableRequest;
import com.github.saphyra.apphub.api.notebook.model.request.EditTableRequest;
import com.github.saphyra.apphub.api.notebook.model.response.TableResponse;
import com.github.saphyra.apphub.api.notebook.server.TableControllerDeprecated;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.api.notebook.model.ListItemType;
import com.github.saphyra.apphub.service.notebook.service.ConvertTableToChecklistTableService;
import com.github.saphyra.apphub.service.notebook.service.table_deprecated.creation.TableCreationService;
import com.github.saphyra.apphub.service.notebook.service.table_deprecated.edition.TableEditionService;
import com.github.saphyra.apphub.service.notebook.service.table_deprecated.query.ContentTableColumnResponseProvider;
import com.github.saphyra.apphub.service.notebook.service.table_deprecated.query.TableQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
public class TableControllerDeprecatedImpl implements TableControllerDeprecated {
    private final TableCreationService tableCreationService;
    private final TableEditionService tableEditionService;
    private final TableQueryService tableQueryService;
    private final ConvertTableToChecklistTableService convertTableToChecklistTableService;
    private final ContentTableColumnResponseProvider tableColumnResponseProvider;

    @Override
    public OneParamResponse<UUID> createTable(CreateTableRequest request, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to create a table.", accessTokenHeader.getUserId());
        return new OneParamResponse<>(tableCreationService.create(request, accessTokenHeader.getUserId(), ListItemType.TABLE));
    }

    @Override
    public TableResponse<String> editTable(EditTableRequest request, UUID listItemId) {
        log.info("Editing table {}", listItemId);
        tableEditionService.edit(listItemId, request);
        return getTable(listItemId);
    }

    @Override
    public TableResponse<String> getTable(UUID listItemId) {
        log.info("Querying table with listItemId {}", listItemId);
        return tableQueryService.getTable(listItemId, tableColumnResponseProvider);
    }

    @Override
    public void convertToChecklistTable(UUID listItemId) {
        log.info("Converting table {} to checklistTable", listItemId);
        convertTableToChecklistTableService.convert(listItemId);
    }
}
