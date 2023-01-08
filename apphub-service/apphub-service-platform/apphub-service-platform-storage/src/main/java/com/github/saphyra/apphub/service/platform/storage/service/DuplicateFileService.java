package com.github.saphyra.apphub.service.platform.storage.service;

import com.github.saphyra.apphub.service.platform.storage.dao.StoredFile;
import com.github.saphyra.apphub.service.platform.storage.service.store.StoreFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class DuplicateFileService {
    private final DownloadFileService downloadFileService;
    private final StoreFileService storeFileService;

    public UUID duplicateFile(UUID userId, UUID storedFileId) {
        DownloadResult existingFile = downloadFileService.downloadFile(userId, storedFileId);

        try {
            StoredFile storedFile = existingFile.getStoredFile();
            UUID fileId = storeFileService.createFile(userId, storedFile.getFileName(), storedFile.getExtension(), storedFile.getSize());
            return storeFileService.uploadFile(userId, fileId, existingFile.getInputStream());
        } finally {
            existingFile.getFtpClient()
                .close();
        }
    }
}
