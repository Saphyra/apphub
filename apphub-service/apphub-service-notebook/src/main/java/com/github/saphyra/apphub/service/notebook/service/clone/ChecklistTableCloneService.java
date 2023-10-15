package com.github.saphyra.apphub.service.notebook.service.clone;

import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.table.row.ChecklistTableRowDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Deprecated
class ChecklistTableCloneService {
    private final TableCloneService tableCloneService;
    private final ChecklistTableRowDao checklistTableRowDao;
    private final CloneUtil cloneUtil;

    public void clone(ListItem toClone, ListItem listItemClone) {
        tableCloneService.clone(toClone, listItemClone);

        checklistTableRowDao.getByParent(toClone.getListItemId())
            .stream()
            .map(checklistTableRow -> cloneUtil.clone(listItemClone.getListItemId(), checklistTableRow))
            .forEach(checklistTableRowDao::save);
    }
}
