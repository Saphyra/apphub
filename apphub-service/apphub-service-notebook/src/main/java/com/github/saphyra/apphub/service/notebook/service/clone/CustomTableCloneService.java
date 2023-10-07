package com.github.saphyra.apphub.service.notebook.service.clone;

import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.file.File;
import com.github.saphyra.apphub.service.notebook.dao.file.FileDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.api.notebook.model.ColumnType;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoin;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoinDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class CustomTableCloneService {
    private final ChecklistTableCloneService checklistTableCloneService;
    private final TableJoinDao tableJoinDao;
    private final FileDao fileDao;
    private final FileCloneService fileCloneService;

    void clone(ListItem toClone, ListItem listItemClone) {
        checklistTableCloneService.clone(toClone, listItemClone);

        List<TableJoin> originalJoins = tableJoinDao.getByParent(toClone.getListItemId())
            .stream()
            .filter(tableJoin -> tableJoin.getColumnType() == ColumnType.IMAGE || tableJoin.getColumnType() == ColumnType.FILE)
            .toList();

        List<TableJoin> clonedJoins = tableJoinDao.getByParent(listItemClone.getListItemId());

        originalJoins.forEach(tableJoin -> clone(toClone.getUserId(), tableJoin, search(tableJoin, clonedJoins)));
    }

    private void clone(UUID userId, TableJoin toClone, TableJoin tableJoinClone) {
        File file = fileDao.findByParent(toClone.getTableJoinId())
            .orElseThrow(() -> ExceptionFactory.reportedException("No file found for TableJoin " + tableJoinClone.getTableJoinId()));

        fileCloneService.cloneFile(userId, tableJoinClone.getTableJoinId(), file);
    }

    private TableJoin search(TableJoin tableJoin, List<TableJoin> clonedJoins) {
        return clonedJoins.stream()
            .filter(tableJoin1 -> tableJoin1.getColumnIndex().equals(tableJoin.getColumnIndex()) && tableJoin1.getRowIndex().equals(tableJoin.getRowIndex()))
            .findFirst()
            .orElseThrow(() -> ExceptionFactory.reportedException("No matching tableJoin found."));
    }
}
