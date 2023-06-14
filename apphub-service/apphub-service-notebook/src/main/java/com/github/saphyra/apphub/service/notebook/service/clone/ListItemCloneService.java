package com.github.saphyra.apphub.service.notebook.service.clone;

import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.service.ListItemFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;

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
    private final FileCloneService cloneFileService;
    private final CustomTableCloneService customTableCloneService;

    @Transactional
    public void clone(UUID listItemId) {
        ListItem listItem = listItemDao.findByIdValidated(listItemId);
        clone(listItem.getParent(), listItem, listItem.getTitle());
    }

    private void clone(UUID parent, ListItem toClone, String title) {
        ListItem listItemClone = listItemFactory.create(toClone.getUserId(), title, parent, toClone.getType(), toClone.isPinned(), toClone.isArchived());
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
            case ONLY_TITLE:
                log.info("OnlyTitle is cloned by default.");
                break;
            case IMAGE:
            case FILE:
                cloneFileService.cloneFile(toClone, listItemClone);
                break;
            case CUSTOM_TABLE: //TODO unit test
                customTableCloneService.clone(toClone, listItemClone);
            default:
                throw ExceptionFactory.reportedException(HttpStatus.NOT_IMPLEMENTED, toClone.getType() + "cannot be cloned.");
        }
    }
}
