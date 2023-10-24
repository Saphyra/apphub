package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.api.notebook.model.table.TableRowModel;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
class EditTableRowService {
    private final EditTableRowDeleter editTableRowDeleter;
    private final EditTableRowEditer editTableRowEditer;

    List<TableFileUploadResponse> editTableRows(ListItem listItem, List<TableRowModel> rows) {
        editTableRowDeleter.deleteRows(listItem.getListItemId(), rows);
        return editTableRowEditer.updateRows(listItem, rows);
    }
}
