package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.service.notebook.dao.file.File;
import com.github.saphyra.apphub.service.notebook.dao.file.FileDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileDeletionService {
    private final FileDao fileDao;
    private final StorageProxy storageProxy;

    public void deleteImage(UUID listItemId) {
        File file = fileDao.findByParentValidated(listItemId);
        storageProxy.deleteFile(file.getStoredFileId());
        fileDao.delete(file);
    }
}
