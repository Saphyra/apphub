package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.lib.common_util.cache.RequestScopedCacheable;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class IsParentArchivedService {
    private final ListItemDao listItemDao;

    @RequestScopedCacheable(cacheNames = "isArchived")
    boolean isAnyOfParentsArchived(UUID parentId) {
        if (isNull(parentId)) {
            return false;
        }

        ListItem listItem = listItemDao.findByIdValidated(parentId);

        if (listItem.isArchived()) {
            return true;
        }

        return isAnyOfParentsArchived(listItem.getParent());
    }
}
