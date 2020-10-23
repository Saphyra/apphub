package com.github.saphyra.apphub.service.notebook.service.clone;

import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoin;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoinDao;
import com.github.saphyra.apphub.service.notebook.service.ContentFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class TableJoinCloneService {
    private final CloneUtil cloneUtil;
    private final ContentDao contentDao;
    private final ContentFactory contentFactory;
    private final TableJoinDao tableJoinDao;

    void clone(ListItem clone, TableJoin tableJoin) {
        TableJoin tableJoinClone = cloneUtil.clone(clone.getListItemId(), tableJoin);
        tableJoinDao.save(tableJoinClone);

        Content tableJoinContent = contentDao.findByParentValidated(tableJoin.getTableJoinId());
        Content tableJoinContentClone = contentFactory.create(tableJoinClone.getTableJoinId(), clone.getUserId(), tableJoinContent.getContent());
        contentDao.save(tableJoinContentClone);
    }
}
