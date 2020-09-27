package com.github.saphyra.apphub.service.notebook.service.table;

import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.table.head.TableHeadDao;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoinDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
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
