package com.github.saphyra.apphub.service.notebook.service.checklist_table;

import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRowDao;
import com.github.saphyra.apphub.service.notebook.service.table.TableDeletionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
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
