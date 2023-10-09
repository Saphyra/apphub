package com.github.saphyra.apphub.service.notebook.service.checklist_table_deprecated.edition;

import com.github.saphyra.apphub.api.notebook.model.request.EditChecklistTableRowRequest;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRowDao;
import com.github.saphyra.apphub.service.notebook.service.checklist_table_deprecated.ChecklistTableRowFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class EditChecklistTableCreateAndUpdateRowService {
    private final ChecklistTableRowDao checklistTableRowDao;
    private final ChecklistTableRowFactory checklistTableRowFactory;

    void createAndUpdate(List<EditChecklistTableRowRequest> rows, ListItem listItem) {
        rows.forEach(editChecklistTableRowRequest -> createOrUpdate(editChecklistTableRowRequest, listItem));
    }

    private void createOrUpdate(EditChecklistTableRowRequest row, ListItem listItem) {
        checklistTableRowDao.findById(row.getRowId())
            .or(() -> Optional.of(checklistTableRowFactory.create(row.getRowId(), listItem.getUserId(), listItem.getListItemId(), row.getRowIndex(), row.getChecked())))
            .ifPresent(checklistTableRow -> {
                checklistTableRow.setChecked(row.getChecked());
                checklistTableRowDao.save(checklistTableRow);
            });
    }
}
