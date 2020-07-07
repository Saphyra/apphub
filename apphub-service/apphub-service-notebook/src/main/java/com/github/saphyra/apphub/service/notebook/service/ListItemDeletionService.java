package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.lib.exception.NotImplementedException;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
//TODO unit test
public class ListItemDeletionService {
    private final ListItemDao listItemDao;

    public void deleteListItem(UUID listItemId, UUID userId) {
        ListItem listItem = listItemDao.findByIdValidated(listItemId);
        deleteChild(listItem, userId);
    }

    private void deleteChild(ListItem listItem, UUID userId) {
        switch (listItem.getType()) {
            case CATEGORY:
                deleteChildren(listItem, userId);
                break;
            default:
                throw new NotImplementedException("Unhandled listItemType: " + listItem.getType());
        }

        listItemDao.delete(listItem);
    }

    private void deleteChildren(ListItem category, UUID userId) {
        listItemDao.getByUserIdAndParent(userId, category.getListItemId())
            .stream()
            .peek(listItem -> deleteChild(listItem, userId))
            .forEach(listItem -> deleteChild(listItem, userId));
    }
}
