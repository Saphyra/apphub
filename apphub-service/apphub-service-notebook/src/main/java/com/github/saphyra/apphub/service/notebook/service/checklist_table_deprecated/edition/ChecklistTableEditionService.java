package com.github.saphyra.apphub.service.notebook.service.checklist_table_deprecated.edition;

import com.github.saphyra.apphub.api.notebook.model.request.EditChecklistTableRequest;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.service.table_deprecated.edition.TableEditionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
@Slf4j
public class ChecklistTableEditionService {
    private final TableEditionService tableEditionService;
    private final EditChecklistTableRowDeletionService editChecklistTableRowDeletionService;
    private final EditChecklistTableCreateAndUpdateRowService editChecklistTableCreateAndUpdateRowService;

    @Transactional
    public void edit(UUID listItemId, EditChecklistTableRequest request) {
        ListItem checklistTable = tableEditionService.edit(listItemId, request.getTable());

        editChecklistTableRowDeletionService.deleteChecklistTableRows(request.getRows(), listItemId);
        editChecklistTableCreateAndUpdateRowService.createAndUpdate(request.getRows(), checklistTable);
    }
}
