package com.github.saphyra.apphub.service.notebook.service.clone;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemFactory;
import com.github.saphyra.apphub.service.notebook.service.StorageProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class FileCloneService {
    private final ListItemDao listItemDao;
    private final ListItemFactory listItemFactory;
    private final StorageProxy storageProxy;
    private final UuidConverter uuidConverter;

    void cloneFile(UUID parent, ListItem toClone) {
        UUID originalStoredFileId = uuidConverter.convertEntity(toClone.getData());
        UUID clonedStoredFileId = storageProxy.cloneFile(originalStoredFileId);

        ListItem clone = listItemFactory.clone(parent, toClone, uuidConverter.convertDomain(clonedStoredFileId));
        listItemDao.save(clone);
    }
}
