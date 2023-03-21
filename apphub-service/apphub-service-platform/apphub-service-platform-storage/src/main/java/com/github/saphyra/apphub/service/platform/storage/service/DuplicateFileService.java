package com.github.saphyra.apphub.service.platform.storage.service;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.platform.storage.dao.StoredFile;
import com.github.saphyra.apphub.service.platform.storage.dao.StoredFileDao;
import com.github.saphyra.apphub.service.platform.storage.service.store.StoreFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DuplicateFileService {
    private final DownloadFileService downloadFileService;
    private final StoreFileService storeFileService;
    private final StoredFileDao storedFileDao;
    private final ExecutorServiceBean executorServiceBean;
    private final AccessTokenProvider accessTokenProvider;

    public UUID duplicateFile(UUID userId, UUID storedFileId) {
        StoredFile storedFile = storedFileDao.findByIdValidated(storedFileId);
        UUID newStoredFileId = storeFileService.createFile(userId, storedFile.getFileName(), storedFile.getSize());

        AccessTokenHeader accessTokenHeader = accessTokenProvider.get();

        executorServiceBean.execute(() -> {
            accessTokenProvider.set(accessTokenHeader);

            DownloadResult existingFile = downloadFileService.downloadFile(userId, storedFileId);
            try {
                storeFileService.uploadFile(userId, newStoredFileId, existingFile.getInputStream(), 0L);
            } finally {
                existingFile.getFtpClient()
                    .close();
                accessTokenProvider.clear();
            }
        });

        return newStoredFileId;
    }
}
