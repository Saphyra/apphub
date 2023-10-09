package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.service.checklist.ChecklistDeletionService;
import com.github.saphyra.apphub.service.notebook.service.checklist_table_deprecated.ChecklistTableDeletionService;
import com.github.saphyra.apphub.service.notebook.service.custom_table_deprecated.CustomTableDeletionService;
import com.github.saphyra.apphub.service.notebook.service.table_deprecated.TableDeletionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class ListItemDeletionService {
    private final ListItemDao listItemDao;
    private final ContentDao contentDao;
    private final TableDeletionService tableDeletionService;
    private final ChecklistTableDeletionService checklistTableDeletionService;
    private final FileDeletionService fileDeletionService;
    private final CustomTableDeletionService customTableDeletionService;
    private final ChecklistDeletionService checklistDeletionService;

    @Transactional
    public void deleteListItem(UUID listItemId, UUID userId) {
        ListItem listItem = listItemDao.findByIdValidated(listItemId);
        deleteChild(listItem, userId);
    }

    private void deleteChild(ListItem listItem, UUID userId) {
        switch (listItem.getType()) {
            case CATEGORY -> deleteChildren(listItem, userId);
            case CHECKLIST -> checklistDeletionService.delete(listItem.getListItemId());
            case TEXT, LINK -> contentDao.deleteByParent(listItem.getListItemId());
            case TABLE -> tableDeletionService.deleteByListItemId(listItem.getListItemId());
            case CHECKLIST_TABLE -> checklistTableDeletionService.deleteByListItemId(listItem.getListItemId());
            case ONLY_TITLE -> log.info("OnlyTitle is handled by default.");
            case IMAGE, FILE -> fileDeletionService.deleteFile(listItem.getListItemId());
            case CUSTOM_TABLE -> customTableDeletionService.delete(listItem);
            default -> throw ExceptionFactory.reportedException(HttpStatus.NOT_IMPLEMENTED, "Unhandled listItemType: " + listItem.getType());
        }

        listItemDao.delete(listItem);
    }

    private void deleteChildren(ListItem category, UUID userId) {
        listItemDao.getByUserIdAndParent(userId, category.getListItemId())
            .forEach(listItem -> deleteChild(listItem, userId));
    }
}
