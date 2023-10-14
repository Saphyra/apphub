package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.notebook.model.table.EditTableRequest;
import com.github.saphyra.apphub.api.notebook.model.table.EditTableResponse;
import com.github.saphyra.apphub.api.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.service.table.TableQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class TableEditionService {
    private final EditTableRequestValidator editTableRequestValidator;
    private final ListItemDao listItemDao;
    private final TableQueryService tableQueryService;
    private final EditTableHeadService editTableHeadService;
    private final EditTableRowService editTableRowService;

    public EditTableResponse editTable(UUID listItemId, EditTableRequest request) {
        editTableRequestValidator.validate(listItemId, request);

        ListItem listItem = listItemDao.findByIdValidated(listItemId);
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
