package com.github.saphyra.apphub.service.notebook.service.checklist_table_deprecated;

import com.github.saphyra.apphub.api.notebook.model.response.ChecklistTableResponse;
import com.github.saphyra.apphub.api.notebook.model.response.ChecklistTableRowStatusResponse;
import com.github.saphyra.apphub.api.notebook.model.response.TableResponse;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRow;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRowDao;
import com.github.saphyra.apphub.service.notebook.service.table_deprecated.query.ContentTableColumnResponseProvider;
import com.github.saphyra.apphub.service.notebook.service.table_deprecated.query.TableQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class ChecklistTableQueryService {
    private final TableQueryService tableQueryService;
    private final ChecklistTableRowDao checklistTableRowDao;
    private final ContentTableColumnResponseProvider contentTableColumnResponseProvider;

    public ChecklistTableResponse getChecklistTable(UUID listItemId) {
        TableResponse<String> tableResponse = tableQueryService.getTable(listItemId, contentTableColumnResponseProvider);
        Map<Integer, ChecklistTableRowStatusResponse> rowStatus = checklistTableRowDao.getByParent(listItemId)
            .stream()
            .collect(Collectors.toMap(ChecklistTableRow::getRowIndex, this::getRowStatusResponse));
        return ChecklistTableResponse.builder()
            .title(tableResponse.getTitle())
            .parent(tableResponse.getParent())
            .tableHeads(tableResponse.getTableHeads())
            .tableColumns(tableResponse.getTableColumns())
            .rowStatus(rowStatus)
            .build();
    }

    private ChecklistTableRowStatusResponse getRowStatusResponse(ChecklistTableRow row) {
        return ChecklistTableRowStatusResponse.builder()
            .rowId(row.getRowId())
            .checked(row.isChecked())
            .build();
    }
}
