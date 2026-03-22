package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ArchiveService {
    private final ListItemDao listItemDao;

    public void archive(UUID listItemId, Boolean archived) {
        ValidationUtil.notNull(archived, "archived");

        ListItem listItem = listItemDao.findByIdValidated(listItemId);
        listItem.setArchived(archived);
        listItemDao.save(listItem);
    }
}
