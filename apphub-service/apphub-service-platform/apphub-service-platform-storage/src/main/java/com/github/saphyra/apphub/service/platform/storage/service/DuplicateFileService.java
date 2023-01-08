package com.github.saphyra.apphub.service.platform.storage.service;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.platform.storage.dao.StoredFile;
import com.github.saphyra.apphub.service.platform.storage.service.store.StoreFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class DuplicateFileService {
    private final DownloadFileService downloadFileService;
    private final StoreFileService storeFileService;

    public UUID duplicateFile(UUID userId, UUID storedFileId) {
        BiWrapper<InputStream, StoredFile> existingFile = downloadFileService.downloadFile(userId, storedFileId);

        UUID fileId = storeFileService.createFile(userId, existingFile.getEntity2().getExtension(), existingFile.getEntity2().getSize());
        return storeFileService.uploadFile(userId, fileId, existingFile.getEntity1());
    }
}
