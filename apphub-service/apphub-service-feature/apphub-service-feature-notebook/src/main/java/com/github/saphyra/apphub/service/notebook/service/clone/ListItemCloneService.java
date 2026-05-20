package com.github.saphyra.apphub.service.notebook.service.clone;

import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
//TODO unit test
public class ListItemCloneService {
    private final ListItemDao listItemDao;
    private final ListItemFactory listItemFactory;
    private final TableCloneService tableCloneService;
    private final DefaultListItemCloneService defaultListItemCloneService;
    private final ChecklistCloneService checklistCloneService;
    private final FileCloneService cloneFileService;

    public void clone(UUID userId, UUID listItemId) {
        ListItem listItem = listItemDao.findByIdValidated(userId, listItemId);
        clone(listItem.getParent(), listItem);
    }

    private void clone(UUID parent, ListItem toClone) {
        switch (toClone.getType()) {
            case CATEGORY -> {
                ListItem listItemClone = listItemFactory.clone(parent, toClone);
                listItemDao.saveListItem(listItemClone);

                listItemDao.getByUserIdAndParent(toClone.getUserId(), toClone.getListItemId())
                    .forEach(listItem -> clone(listItemClone.getListItemId(), listItem));
            }
            case LINK, TEXT, ONLY_TITLE -> defaultListItemCloneService.clone(parent, toClone);
            case CHECKLIST -> checklistCloneService.clone(parent, toClone);
            case TABLE, CHECKLIST_TABLE, CUSTOM_TABLE -> tableCloneService.cloneTable(parent, toClone);
            case IMAGE, FILE -> cloneFileService.cloneFile(parent, toClone);
            default -> throw ExceptionFactory.reportedException(HttpStatus.NOT_IMPLEMENTED, toClone.getType() + " cannot be cloned.");
        }
    }
}
