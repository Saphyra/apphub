package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.checklist_item.ChecklistItemDao;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.service.checklist_table.ChecklistTableDeletionService;
import com.github.saphyra.apphub.service.notebook.service.table.TableDeletionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class ListItemDeletionService {
    private final ListItemDao listItemDao;
    private final ContentDao contentDao;
    private final ChecklistItemDao checklistItemDao;
    private final TableDeletionService tableDeletionService;
    private final ChecklistTableDeletionService checklistTableDeletionService;
    private final FileDeletionService fileDeletionService;

    @Transactional
    public void deleteListItem(UUID listItemId, UUID userId) {
        ListItem listItem = listItemDao.findByIdValidated(listItemId);
        deleteChild(listItem, userId);
    }

    private void deleteChild(ListItem listItem, UUID userId) {
        switch (listItem.getType()) {
            case CATEGORY:
                deleteChildren(listItem, userId);
                break;
            case CHECKLIST:
                checklistItemDao.getByParent(listItem.getListItemId())
                    .stream()
                    .peek(checklistItem -> contentDao.deleteByParent(checklistItem.getChecklistItemId()))
                    .forEach(checklistItemDao::delete);
                break;
            case TEXT:
            case LINK:
                contentDao.deleteByParent(listItem.getListItemId());
                break;
            case TABLE:
                tableDeletionService.deleteByListItemId(listItem.getListItemId());
                break;
            case CHECKLIST_TABLE:
                checklistTableDeletionService.deleteByListItemId(listItem.getListItemId());
                break;
            case ONLY_TITLE:
                log.info("OnlyTitle is handled by default.");
                break;
            case IMAGE:
            case FILE:
                fileDeletionService.deleteImage(listItem.getListItemId());
                break;
            default:
                throw ExceptionFactory.reportedException(HttpStatus.NOT_IMPLEMENTED, "Unhandled listItemType: " + listItem.getType());
        }

        listItemDao.delete(listItem);
    }

    private void deleteChildren(ListItem category, UUID userId) {
        listItemDao.getByUserIdAndParent(userId, category.getListItemId())
            .forEach(listItem -> deleteChild(listItem, userId));
    }
}
