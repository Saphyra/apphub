package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.lib.exception.NotImplementedException;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.text.TextDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class ListItemDeletionService {
    private final ListItemDao listItemDao;
    private final TextDao textDao;

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
            case TEXT: //TODO unit test
                textDao.deleteByParent(listItem.getListItemId());
                break;
            default:
                throw new NotImplementedException("Unhandled listItemType: " + listItem.getType());
        }

        listItemDao.delete(listItem);
    }

    private void deleteChildren(ListItem category, UUID userId) {
        listItemDao.getByUserIdAndParent(userId, category.getListItemId())
            .forEach(listItem -> deleteChild(listItem, userId));
    }
}
