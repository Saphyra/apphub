package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.pin.mapping.PinMappingDao;
import com.github.saphyra.apphub.service.notebook.service.checklist.ChecklistDeletionService;
import com.github.saphyra.apphub.service.notebook.service.file.FileDeletionService;
import com.github.saphyra.apphub.service.notebook.service.table.TableDeletionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
//TODO unit test
public class ListItemDeletionService {
    private final ListItemDao listItemDao;
    private final TableDeletionService tableDeletionService;
    private final ChecklistDeletionService checklistDeletionService;
    private final PinMappingDao pinMappingDao;
    private final FileDeletionService fileDeletionService;

    @Transactional
    public void deleteListItem(UUID listItemId, UUID userId) {
        ListItem listItem = listItemDao.findByIdValidated(userId, listItemId);
        deleteChild(listItem, userId);
    }

    private void deleteChild(ListItem listItem, UUID userId) {
        switch (listItem.getType()) {
            case CATEGORY -> {
                deleteChildren(listItem, userId);
                listItemDao.delete(listItem);
            }
            case CHECKLIST -> checklistDeletionService.delete(listItem.getUserId(), listItem.getListItemId());
            case TEXT, LINK, ONLY_TITLE -> listItemDao.delete(listItem);
            case IMAGE, FILE -> fileDeletionService.deleteFile(listItem);
            case TABLE, CHECKLIST_TABLE, CUSTOM_TABLE -> tableDeletionService.delete(listItem);
            default -> throw ExceptionFactory.reportedException(HttpStatus.NOT_IMPLEMENTED, "Unhandled listItemType: " + listItem.getType());
        }

        pinMappingDao.deleteByListItemId(listItem.getListItemId());
    }

    private void deleteChildren(ListItem category, UUID userId) {
        listItemDao.getByUserIdAndParent(userId, category.getListItemId())
            .forEach(listItem -> deleteChild(listItem, userId));
    }
}
