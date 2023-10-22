package com.github.saphyra.apphub.service.notebook.service.clone.table;

import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.table_head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.table_head.TableHeadDao;
import com.github.saphyra.apphub.service.notebook.dao.table_head.TableHeadFactory;
import com.github.saphyra.apphub.service.notebook.service.ContentFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class TableHeadCloneService {
    private final ContentDao contentDao;
    private final ContentFactory contentFactory;
    private final TableHeadDao tableHeadDao;
    private final TableHeadFactory tableHeadFactory;

    void cloneTableHeads(ListItem original, ListItem clone) {
        tableHeadDao.getByParent(original.getListItemId())
            .forEach(tableHead -> cloneTableHead(clone, tableHead));
    }

    private void cloneTableHead(ListItem clone, TableHead tableHead) {
        TableHead tableHeadClone = tableHeadFactory.create(clone.getUserId(), clone.getListItemId(), tableHead.getColumnIndex());
        tableHeadDao.save(tableHeadClone);

        Content tableHeadContent = contentDao.findByParentValidated(tableHead.getTableHeadId());
        Content tableHeadContentClone = contentFactory.create(clone.getListItemId(), tableHeadClone.getTableHeadId(), clone.getUserId(), tableHeadContent.getContent());
        contentDao.save(tableHeadContentClone);
    }
}
