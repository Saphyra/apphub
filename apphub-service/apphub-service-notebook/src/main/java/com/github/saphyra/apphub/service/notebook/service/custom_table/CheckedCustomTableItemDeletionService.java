package com.github.saphyra.apphub.service.notebook.service.custom_table;

import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.api.notebook.model.ColumnType;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoin;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoinDao;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRow;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRowDao;
import com.github.saphyra.apphub.service.notebook.service.FileDeletionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CheckedCustomTableItemDeletionService {
    private final ChecklistTableRowDao checklistTableRowDao;
    private final TableJoinDao tableJoinDao;
    private final ContentDao contentDao;
    private final FileDeletionService fileDeletionService;

    @Transactional
    public void deleteCheckedItems(UUID listItemId) {
        checklistTableRowDao.getByParent(listItemId)
            .stream()
            .filter(ChecklistTableRow::isChecked)
            .forEach(this::delete);
    }

    private void delete(ChecklistTableRow checklistTableRow) {
        tableJoinDao.getByParent(checklistTableRow.getParent())
            .stream()
            .filter(tableJoin -> tableJoin.getRowIndex().equals(checklistTableRow.getRowIndex()))
            .forEach(this::delete);

        checklistTableRowDao.delete(checklistTableRow);
    }

    private void delete(TableJoin tableJoin) {
        if (tableJoin.getColumnType() == ColumnType.FILE || tableJoin.getColumnType() == ColumnType.IMAGE) {
            fileDeletionService.deleteFile(tableJoin.getTableJoinId());
        } else {
            contentDao.deleteByParent(tableJoin.getTableJoinId());
        }

        tableJoinDao.delete(tableJoin);
    }
}
