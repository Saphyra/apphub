package com.github.saphyra.apphub.service.platform.storage.event;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.platform.storage.dao.StoredFileDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
class StoredFileCleanupEventProcessor {
    private final DateTimeUtil dateTimeUtil;
    private final StoredFileDao storedFileDao;
    private final CleanupProperties cleanupProperties;

    void cleanup() {
        LocalDateTime expirationTime = dateTimeUtil.getCurrentDateTime()
            .minusSeconds(cleanupProperties.getExpirationSeconds());

        storedFileDao.deleteExpired(expirationTime);
    }
}
