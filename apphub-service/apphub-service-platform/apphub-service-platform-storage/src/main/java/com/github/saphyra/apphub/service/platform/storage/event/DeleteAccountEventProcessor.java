package com.github.saphyra.apphub.service.platform.storage.event;

import com.github.saphyra.apphub.service.platform.storage.dao.StoredFileDao;
import com.github.saphyra.apphub.service.platform.storage.service.DeleteFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class DeleteAccountEventProcessor {
    private final StoredFileDao storedFileDao;
    private final DeleteFileService deleteFileService;

    void deleteUserData(UUID userId) {
        storedFileDao.getByUserId(userId)
            .forEach(storedFile -> deleteFileService.deleteFile(userId, storedFile));
    }
}
