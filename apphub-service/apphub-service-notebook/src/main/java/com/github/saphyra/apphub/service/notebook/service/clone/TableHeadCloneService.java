package com.github.saphyra.apphub.service.notebook.service.clone;

import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.table.head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.table.head.TableHeadDao;
import com.github.saphyra.apphub.service.notebook.service.ContentFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class TableHeadCloneService {
    private final CloneUtil cloneUtil;
    private final ContentDao contentDao;
    private final ContentFactory contentFactory;
    private final TableHeadDao tableHeadDao;

    void clone(ListItem clone, TableHead tableHead) {
        TableHead tableHeadClone = cloneUtil.clone(clone.getListItemId(), tableHead);
        tableHeadDao.save(tableHeadClone);

        Content tableHeadContent = contentDao.findByParentValidated(tableHead.getTableHeadId());
        Content tableHeadContentClone = contentFactory.create(tableHeadClone.getTableHeadId(), clone.getUserId(), tableHeadContent.getContent());
        contentDao.save(tableHeadContentClone);
    }
}
