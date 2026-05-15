package com.github.saphyra.apphub.service.platform.storage.dao;

import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.platform.storage.dao.deprecated.DeprecatedStoredFileDao;
import com.github.saphyra.apphub.service.platform.storage.dao.deprecated.StoredFileView;
import com.github.saphyra.apphub.service.platform.storage.dao.stored_file.Storage;
import com.github.saphyra.apphub.service.platform.storage.dao.stored_file.StoredFile;
import com.github.saphyra.apphub.service.platform.storage.dao.stored_file.StoredFileDao;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class StoredFileMigrationService {
    private final DeprecatedStoredFileDao deprecatedStoredFileDao;
    private final StoredFileDao storedFileDao;
    private final AccessTokenProvider accessTokenProvider;
    private final ErrorReporterService errorReporterService;
    private final UuidConverter uuidConverter;

    @PostConstruct
    void migrate() {
        log.info("Starting stored file migration");

        Arrays.stream(Storage.values())
            .flatMap(storage -> deprecatedStoredFileDao.getViewsByStorage(storage).stream())
            .forEach(this::migrate);

        log.info("Finished stored file migration");
    }

    private void migrate(StoredFileView view) {
        try {
            AccessToken accessToken = AccessToken.builder()
                .userId(uuidConverter.convertEntity(view.getUserId()))
                .build();
            try (AutoCloseable _ = accessTokenProvider.set(accessToken)) {
                StoredFile storedFile = deprecatedStoredFileDao.findByIdValidated(uuidConverter.convertEntity(view.getStoredFileId()));

                storedFileDao.save(storedFile);
            }
        } catch (Exception e) {
            errorReporterService.report("Failed migrating storedFile " + view.getStoredFileId(), e);
        }
    }
}
