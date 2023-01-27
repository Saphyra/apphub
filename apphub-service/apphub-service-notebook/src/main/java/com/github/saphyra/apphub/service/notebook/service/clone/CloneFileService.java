package com.github.saphyra.apphub.service.notebook.service.clone;

import com.github.saphyra.apphub.service.notebook.dao.file.File;
import com.github.saphyra.apphub.service.notebook.dao.file.FileDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.service.StorageProxy;
import com.github.saphyra.apphub.service.notebook.service.FileFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class CloneFileService {
    private final FileDao fileDao;
    private final StorageProxy storageProxy;
    private final FileFactory fileFactory;

    void cloneFile(ListItem toClone, ListItem listItemClone) {
        File fileToClone = fileDao.findByParentValidated(toClone.getListItemId());

        UUID fileId = storageProxy.duplicateFile(fileToClone.getStoredFileId());

        File file = fileFactory.create(toClone.getUserId(), listItemClone.getListItemId(), fileId);

        fileDao.save(file);
    }
}
