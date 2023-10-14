package com.github.saphyra.apphub.service.notebook.service.table_deprecated;

import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoinDao;
import com.github.saphyra.apphub.service.notebook.dao.table_head.TableHeadDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class TableDeletionService {
    private final TableHeadDao tableHeadDao;
    private final TableJoinDao tableJoinDao;
    private final ContentDao contentDao;

    public void deleteByListItemId(UUID listItemId) {
        deleteTableHeads(listItemId);
        deleteTableJoins(listItemId);
    }

    private void deleteTableHeads(UUID listItemId) {
        tableHeadDao.getByParent(listItemId)
            .forEach(tableHead -> {
                contentDao.deleteByParent(tableHead.getTableHeadId());
                tableHeadDao.delete(tableHead);
            });
    }

    private void deleteTableJoins(UUID listItemId) {
        tableJoinDao.getByParent(listItemId)
            .forEach(tableJoin -> {
                contentDao.deleteByParent(tableJoin.getTableJoinId());
                tableJoinDao.delete(tableJoin);
            });
    }
}
