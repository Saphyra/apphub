package com.github.saphyra.apphub.service.notebook.service.clone;

import com.github.saphyra.apphub.lib.exception.NotImplementedException;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItem;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItemDao;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.table.head.TableHead;
import com.github.saphyra.apphub.service.notebook.dao.table.head.TableHeadDao;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoin;
import com.github.saphyra.apphub.service.notebook.dao.table.join.TableJoinDao;
import com.github.saphyra.apphub.service.notebook.service.ContentFactory;
import com.github.saphyra.apphub.service.notebook.service.ListItemFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
//TODO refactor - split
//TODO unit test
public class ListItemCloneService {
    private final CloneUtil cloneUtil;
    private final ChecklistItemDao checklistItemDao;
    private final ContentDao contentDao;
    private final ContentFactory contentFactory;
    private final ListItemDao listItemDao;
    private final ListItemFactory listItemFactory;
    private final TableHeadDao tableHeadDao;
    private final TableJoinDao tableJoinDao;

    @Transactional
    public void clone(UUID listItemId) {
        ListItem listItem = listItemDao.findByIdValidated(listItemId);
        clone(listItem.getParent(), listItem, listItem.getTitle());
    }

    private void clone(UUID parent, ListItem toClone, String title) {
        ListItem listItemClone = listItemFactory.create(toClone.getUserId(), title, parent, toClone.getType());
        listItemDao.save(listItemClone);
        switch (toClone.getType()) {
            case CATEGORY:
                listItemDao.getByUserIdAndParent(toClone.getUserId(), toClone.getListItemId())
                    .forEach(listItem -> clone(listItemClone.getListItemId(), listItem, listItem.getTitle()));
                break;
            case LINK:
            case TEXT:
                Content content = contentDao.findByParentValidated(toClone.getListItemId());
                Content clone = contentFactory.create(listItemClone, content.getContent());
                contentDao.save(clone);
                break;
            case CHECKLIST:
                checklistItemDao.getByParent(toClone.getListItemId())
                    .forEach(checklistItem -> {
                        ChecklistItem checklistItemClone = cloneUtil.clone(listItemClone.getListItemId(), checklistItem);
                        checklistItemDao.save(checklistItemClone);

                        Content checklistItemContent = contentDao.findByParentValidated(checklistItem.getChecklistItemId());
                        Content checklistItemContentClone = contentFactory.create(checklistItemClone.getChecklistItemId(), toClone.getUserId(), checklistItemContent.getContent());
                        contentDao.save(checklistItemContentClone);
                    });
            case TABLE:
                tableHeadDao.getByParent(toClone.getListItemId())
                    .forEach(tableHead -> {
                        TableHead tableHeadClone = cloneUtil.clone(listItemClone.getListItemId(), tableHead);
                        tableHeadDao.save(tableHeadClone);

                        Content tableHeadContent = contentDao.findByParentValidated(tableHead.getTableHeadId());
                        Content tableHeadContentClone = contentFactory.create(tableHeadClone.getTableHeadId(), toClone.getUserId(), tableHeadContent.getContent());
                        contentDao.save(tableHeadContentClone);
                    });
                tableJoinDao.getByParent(toClone.getListItemId())
                    .forEach(tableJoin -> {
                        TableJoin tableJoinClone = cloneUtil.clone(listItemClone.getListItemId(), tableJoin);
                        tableJoinDao.save(tableJoinClone);

                        Content tableJoinContent = contentDao.findByParentValidated(tableJoin.getTableJoinId());
                        Content tableJoinContentClone = contentFactory.create(tableJoinClone.getTableJoinId(), toClone.getUserId(), tableJoinContent.getContent());
                        contentDao.save(tableJoinContentClone);
                    });
                break;
            case CHECKLIST_TABLE:
            default:
                throw new NotImplementedException(toClone.getType() + " cannot be cloned.");
        }
    }
}
