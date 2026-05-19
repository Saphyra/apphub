package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.feature.notebook.model.table.EditTableRequest;
import com.github.saphyra.apphub.api.feature.notebook.model.table.EditTableResponse;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItem;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItemDao;
import com.github.saphyra.apphub.service.notebook.service.table.query.TableQueryService;
import com.github.saphyra.apphub.service.notebook.service.table.validator.EditTableRequestValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class TableEditionService {
    private final EditTableRequestValidator editTableRequestValidator;
    private final DeprecatedListItemDao listItemDao;
    private final TableQueryService tableQueryService;
    private final EditTableHeadService editTableHeadService;
    private final EditTableRowService editTableRowService;

    @Transactional
    public EditTableResponse editTable(UUID listItemId, EditTableRequest request) {
        editTableRequestValidator.validate(listItemId, request);

        DeprecatedListItem listItem = listItemDao.findByIdValidated(listItemId);
        listItem.setTitle(request.getTitle());
        listItemDao.save(listItem);

        editTableHeadService.editTableHeads(listItem, request.getTableHeads());
        List<TableFileUploadResponse> fileUploads = editTableRowService.editTableRows(listItem, request.getRows());

        return EditTableResponse.builder()
            .tableResponse(tableQueryService.getTable(listItemId))
            .fileUpload(fileUploads)
            .build();
    }
}
