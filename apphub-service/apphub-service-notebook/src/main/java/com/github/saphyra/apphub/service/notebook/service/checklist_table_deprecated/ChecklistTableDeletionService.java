package com.github.saphyra.apphub.service.notebook.service.checklist_table_deprecated;

import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRowDao;
import com.github.saphyra.apphub.service.notebook.service.table_deprecated.TableDeletionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class ChecklistTableDeletionService {
    private final TableDeletionService tableDeletionService;
    private final ChecklistTableRowDao checklistTableRowDao;

    public void deleteByListItemId(UUID listItemId){
        tableDeletionService.deleteByListItemId(listItemId);
        checklistTableRowDao.deleteByParent(listItemId);
    }
}
