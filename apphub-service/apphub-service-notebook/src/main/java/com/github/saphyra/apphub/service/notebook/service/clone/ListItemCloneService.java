package com.github.saphyra.apphub.service.notebook.service.clone;

import com.github.saphyra.apphub.lib.exception.NotImplementedException;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.service.ListItemFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class ListItemCloneService {
    private final ListItemDao listItemDao;
    private final ListItemFactory listItemFactory;
    private final TableCloneService tableCloneService;
    private final TextAndLinkCloneService textAndLinkCloneService;
    private final ChecklistCloneService checklistCloneService;
    private final ChecklistTableCloneService checklistTableCloneService;

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
                textAndLinkCloneService.clone(toClone.getListItemId(), listItemClone);
                break;
            case CHECKLIST:
                checklistCloneService.clone(toClone, listItemClone);
                break;
            case TABLE:
                tableCloneService.clone(toClone, listItemClone);
                break;
            case CHECKLIST_TABLE:
                checklistTableCloneService.clone(toClone, listItemClone);
                break;
            default:
                throw new NotImplementedException(toClone.getType() + " cannot be cloned.");
        }
    }
}
