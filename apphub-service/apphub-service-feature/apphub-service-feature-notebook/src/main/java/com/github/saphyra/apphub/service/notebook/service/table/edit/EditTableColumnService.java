package com.github.saphyra.apphub.service.notebook.service.table.edit;

import com.github.saphyra.apphub.api.feature.notebook.model.table.TableColumnModel;
import com.github.saphyra.apphub.api.feature.notebook.model.table.TableFileUploadResponse;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class EditTableColumnService {
    private final EditTableColumnDeleter editTableColumnDeleter;
    private final EditTableColumnEditer editTableColumnEditer;

    List<TableFileUploadResponse> editTableColumns(DeprecatedListItem listItem, UUID rowId, List<TableColumnModel> columns) {
        editTableColumnDeleter.deleteColumns(rowId, columns);

        return columns.stream()
            .flatMap(column -> editTableColumnEditer.editTableColumn(listItem, rowId, column).stream())
            .collect(Collectors.toList());
    }
}
