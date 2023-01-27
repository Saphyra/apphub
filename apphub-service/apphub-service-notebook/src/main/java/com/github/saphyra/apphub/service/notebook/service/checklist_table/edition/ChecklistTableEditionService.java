package com.github.saphyra.apphub.service.notebook.service.checklist_table.edition;

import com.github.saphyra.apphub.api.notebook.model.request.EditChecklistTableRequest;
import com.github.saphyra.apphub.api.notebook.model.request.EditTableRequest;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRow;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRowDao;
import com.github.saphyra.apphub.service.notebook.service.checklist_table.ChecklistTableRowFactory;
import com.github.saphyra.apphub.service.notebook.service.table.edition.TableEditionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Component
@Slf4j
public class ChecklistTableEditionService {
    private final TableEditionService tableEditionService;
    private final EditTableRequestConverter editTableRequestConverter;
    private final ChecklistTableRowDao checklistTableRowDao;
    private final ChecklistTableRowFactory checklistTableRowFactory;

    @Transactional
    public void edit(UUID listItemId, EditChecklistTableRequest request) {
        EditTableRequest editTableRequest = editTableRequestConverter.convert(request);
        ListItem checklistTable = tableEditionService.edit(listItemId, editTableRequest);

        int rowIndex;
        for (rowIndex = 0; rowIndex < request.getRows().size(); rowIndex++) {
            int ri = rowIndex;
            boolean checked = request.getRows()
                .get(rowIndex)
                .isChecked();
            ChecklistTableRow row = checklistTableRowDao.findByParentAndRowIndex(listItemId, rowIndex)
                .map(checklistTableRow -> {
                    checklistTableRow.setChecked(checked);
                    return checklistTableRow;
                })
                .orElseGet(() -> checklistTableRowFactory.create(checklistTable.getUserId(), listItemId, ri, checked));
            checklistTableRowDao.save(row);
        }

        checklistTableRowDao.deleteByParentAndRowIndexGreaterThanEqual(listItemId, rowIndex);
    }
}
