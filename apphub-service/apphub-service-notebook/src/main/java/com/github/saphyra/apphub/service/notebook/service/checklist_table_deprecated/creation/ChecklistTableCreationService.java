package com.github.saphyra.apphub.service.notebook.service.checklist_table_deprecated.creation;

import com.github.saphyra.apphub.api.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.notebook.model.request.CreateChecklistTableRequest;
import com.github.saphyra.apphub.api.notebook.model.request.CreateTableRequest;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRow;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRowDao;
import com.github.saphyra.apphub.service.notebook.service.checklist_table_deprecated.ChecklistTableRowFactory;
import com.github.saphyra.apphub.service.notebook.service.table_deprecated.creation.TableCreationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class ChecklistTableCreationService {
    private final TableCreationService tableCreationService;
    private final CreateTableRequestConverter createTableRequestConverter;
    private final ChecklistTableRowFactory rowFactory;
    private final ChecklistTableRowDao checklistTableRowDao;

    @Transactional
    public UUID create(UUID userId, CreateChecklistTableRequest request) {
        CreateTableRequest createTableRequest = createTableRequestConverter.convert(request);
        UUID listItemId = tableCreationService.create(createTableRequest, userId, ListItemType.CHECKLIST_TABLE);

        for (int rowIndex = 0; rowIndex < request.getRows().size(); rowIndex++) {
            boolean rowChecked = request.getRows()
                .get(rowIndex)
                .isChecked();
            ChecklistTableRow row = rowFactory.create(userId, listItemId, rowIndex, rowChecked);
            checklistTableRowDao.save(row);
        }
        return listItemId;
    }
}
