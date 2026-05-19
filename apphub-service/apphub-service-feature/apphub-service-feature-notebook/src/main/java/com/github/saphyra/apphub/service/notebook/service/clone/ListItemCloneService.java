package com.github.saphyra.apphub.service.notebook.service.clone;

import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItem;
import com.github.saphyra.apphub.service.notebook.dao.deprecated_list_item.DeprecatedListItemDao;
import com.github.saphyra.apphub.service.notebook.service.DeprecatedListItemFactory;
import com.github.saphyra.apphub.service.notebook.service.clone.table.TableCloneService;
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
    private final DeprecatedListItemDao listItemDao;
    private final DeprecatedListItemFactory listItemFactory;
    private final TableCloneService tableCloneService;
    private final TextAndLinkCloneService textAndLinkCloneService;
    private final ChecklistCloneService checklistCloneService;
    private final FileCloneService cloneFileService;

    @Transactional
    public void clone(UUID listItemId) {
        DeprecatedListItem listItem = listItemDao.findByIdValidated(listItemId);
        clone(listItem.getParent(), listItem, listItem.getTitle());
    }

    private void clone(UUID parent, DeprecatedListItem toClone, String title) {
        DeprecatedListItem listItemClone = listItemFactory.create(toClone.getUserId(), title, parent, toClone.getType(), toClone.isPinned(), toClone.isArchived());
        listItemDao.save(listItemClone);

        switch (toClone.getType()) {
            case CATEGORY -> listItemDao.getByUserIdAndParent(toClone.getUserId(), toClone.getListItemId()).forEach(listItem -> clone(listItemClone.getListItemId(), listItem, listItem.getTitle()));
            case LINK, TEXT -> textAndLinkCloneService.clone(toClone.getListItemId(), listItemClone);
            case CHECKLIST -> checklistCloneService.clone(toClone, listItemClone);
            case TABLE, CHECKLIST_TABLE, CUSTOM_TABLE -> tableCloneService.cloneTable(toClone, listItemClone);
            case ONLY_TITLE -> log.info("OnlyTitle is cloned by default.");
            case IMAGE, FILE -> cloneFileService.cloneFile(toClone, listItemClone);
            default -> throw ExceptionFactory.reportedException(HttpStatus.NOT_IMPLEMENTED, toClone.getType() + " cannot be cloned.");
        }
    }
}
