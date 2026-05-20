package com.github.saphyra.apphub.service.notebook.service.clone;

import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Clones list items that stores all their data in the main record. (ONLY_TITLE, TEXT, LINK)
 */
@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class DefaultListItemCloneService {
    private final ListItemFactory listItemFactory;
    private final ListItemDao listItemDao;

    void clone(UUID parent, ListItem toClone) {
        ListItem clone = listItemFactory.clone(parent, toClone);
        listItemDao.saveListItem(clone);
    }
}
