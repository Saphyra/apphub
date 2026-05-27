package com.github.saphyra.apphub.service.notebook.service.file;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.service.StorageProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileDeletionService {
    private final ListItemDao listItemDao;
    private final UuidConverter uuidConverter;
    private final StorageProxy storageProxy;

    public void deleteFile(ListItem listItem) {
        UUID storedFileId = uuidConverter.convertEntity(listItem.getData());

        storageProxy.deleteFile(storedFileId);

        listItemDao.delete(listItem);
    }
}
