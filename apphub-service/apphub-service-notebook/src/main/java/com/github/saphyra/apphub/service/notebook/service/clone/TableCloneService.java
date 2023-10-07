package com.github.saphyra.apphub.service.notebook.service.clone;

import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.table_head.TableHeadDao;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoinDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class TableCloneService {
    private final TableHeadDao tableHeadDao;
    private final TableJoinDao tableJoinDao;
    private final TableHeadCloneService tableHeadCloneService;
    private final TableJoinCloneService tableJoinCloneService;

    void clone(ListItem original, ListItem clone) {
        tableHeadDao.getByParent(original.getListItemId())
            .forEach(tableHead -> tableHeadCloneService.clone(clone, tableHead));
        tableJoinDao.getByParent(original.getListItemId())
            .forEach(tableJoin -> tableJoinCloneService.clone(clone, tableJoin));
    }
}
