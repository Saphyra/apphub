package com.github.saphyra.apphub.service.notebook.service.checklist_table.edition;

import com.github.saphyra.apphub.api.notebook.model.request.EditChecklistTableRowRequest;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRowDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class EditChecklistTableRowDeletionService {
    private final ChecklistTableRowDao checklistTableRowDao;

    void deleteChecklistTableRows(List<EditChecklistTableRowRequest> rows, UUID listItemId) {
        List<UUID> incomingRowIds = rows.stream()
            .map(EditChecklistTableRowRequest::getRowId)
            .toList();

        checklistTableRowDao.getByParent(listItemId)
            .stream()
            .filter(checklistTableRow -> !incomingRowIds.contains(checklistTableRow.getRowId()))
            .forEach(checklistTableRowDao::delete);
    }
}
