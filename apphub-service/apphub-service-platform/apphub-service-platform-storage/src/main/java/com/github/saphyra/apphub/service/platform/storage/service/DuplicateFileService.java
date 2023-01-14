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
        UUID fileId = storeFileService.createFile(userId, storedFile.getFileName(), storedFile.getExtension(), storedFile.getSize());

        DownloadResult existingFile = downloadFileService.downloadFile(userId, storedFileId);

        AccessTokenHeader accessTokenHeader = accessTokenProvider.get();

        executorServiceBean.execute(() -> {
            try {
                accessTokenProvider.set(accessTokenHeader);
                storeFileService.uploadFile(userId, fileId, existingFile.getInputStream());
            } finally {
                existingFile.getFtpClient()
                    .close();
                accessTokenProvider.clear();
            }
        });

        return fileId;
    }
}
