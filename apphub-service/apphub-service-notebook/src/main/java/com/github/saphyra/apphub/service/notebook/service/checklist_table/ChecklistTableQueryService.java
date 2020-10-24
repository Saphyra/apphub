package com.github.saphyra.apphub.service.notebook.service.checklist_table;

import com.github.saphyra.apphub.api.notebook.model.response.ChecklistTableResponse;
import com.github.saphyra.apphub.api.notebook.model.response.TableResponse;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRow;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRowDao;
import com.github.saphyra.apphub.service.notebook.service.table.TableQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChecklistTableQueryService {
    private final TableQueryService tableQueryService;
    private final ChecklistTableRowDao checklistTableRowDao;

    public ChecklistTableResponse getChecklistTable(UUID listItemId) {
        TableResponse tableResponse = tableQueryService.getTable(listItemId);
        Map<Integer, Boolean> rowStatus = checklistTableRowDao.getByParent(listItemId)
            .stream()
            .collect(Collectors.toMap(ChecklistTableRow::getRowIndex, ChecklistTableRow::isChecked));
        return ChecklistTableResponse.builder()
            .title(tableResponse.getTitle())
            .tableHeads(tableResponse.getTableHeads())
            .tableColumns(tableResponse.getTableColumns())
            .rowStatus(rowStatus)
            .build();
    }
}
