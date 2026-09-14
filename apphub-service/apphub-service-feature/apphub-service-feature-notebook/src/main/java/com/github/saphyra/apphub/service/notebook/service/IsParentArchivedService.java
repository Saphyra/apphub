package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class IsParentArchivedService {
    private final ListItemDao listItemDao;

    boolean isAnyOfParentsArchived(Map<UUID, ListItem> cache, UUID userId, UUID parentId) {
        if (isNull(parentId)) {
            return false;
        }

        ListItem listItem = cache.computeIfAbsent(parentId, id -> listItemDao.findByIdValidated(userId, id));

        if (listItem.isArchived()) {
            return true;
        }

        return isAnyOfParentsArchived(cache, userId, listItem.getParent());
    }
}
