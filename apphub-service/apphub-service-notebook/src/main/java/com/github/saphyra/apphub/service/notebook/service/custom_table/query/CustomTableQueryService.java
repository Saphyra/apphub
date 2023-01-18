package com.github.saphyra.apphub.service.notebook.service.custom_table.query;

import com.github.saphyra.apphub.api.notebook.model.response.CustomTableResponse;
import com.github.saphyra.apphub.api.notebook.model.response.TableResponse;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRow;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRowDao;
import com.github.saphyra.apphub.service.notebook.service.table.query.TableQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class CustomTableQueryService {
    private final CustomTableColumnProvider customTableColumnProvider;
    private final ChecklistTableRowDao checklistTableRowDao;
    private final TableQueryService tableQueryService;

    public CustomTableResponse getCustomTable(UUID listItemId) {
        TableResponse<Object> tableResponse = tableQueryService.getTable(listItemId, customTableColumnProvider);
        Map<Integer, Boolean> rowStatus = checklistTableRowDao.getByParent(listItemId)
            .stream()
            .collect(Collectors.toMap(ChecklistTableRow::getRowIndex, ChecklistTableRow::isChecked));
        return CustomTableResponse.builder()
            .title(tableResponse.getTitle())
            .tableHeads(tableResponse.getTableHeads())
            .tableColumns(tableResponse.getTableColumns())
            .rowStatus(rowStatus)
            .build();
    }
}
