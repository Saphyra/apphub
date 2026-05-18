package com.github.saphyra.apphub.service.platform.storage.service;

import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.platform.storage.client.DownloadResult;
import com.github.saphyra.apphub.service.platform.storage.client.ftp.FtpStorageClient;
import com.github.saphyra.apphub.service.platform.storage.client.s3.S3StorageClient;
import com.github.saphyra.apphub.service.platform.storage.dao.stored_file.Storage;
import com.github.saphyra.apphub.service.platform.storage.dao.stored_file.StoredFile;
import com.github.saphyra.apphub.service.platform.storage.dao.stored_file.StoredFileDao;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Deprecated(forRemoval = true)
@Profile("!test")
class FtpToS3MigrationService {
    private final StoredFileDao storedFileDao;
    private final S3StorageClient s3StorageClient;
    private final FtpStorageClient ftpStorageClient;
    private final ErrorReporterService errorReporterService;
    private final AccessTokenProvider accessTokenProvider;

    @PostConstruct
    void migrate() {
        log.info("Starting migration of stored files from FTP to S3");

        storedFileDao.getFtpFileIds()
            .forEach(bw -> migrate(bw.getEntity1(), bw.getEntity2()));

        log.info("FTP to S3 migration finished.");
    }

    private void migrate(UUID storedFileId, UUID userId) {
        log.info("Migrating StoredFile {} for user {}", storedFileId, userId);
        AccessToken accessToken = AccessToken.builder()
            .userId(userId)
            .build();
        try (
            var _ = accessTokenProvider.set(accessToken);
            DownloadResult ftpFile = ftpStorageClient.download(storedFileId);
        ) {
            StoredFile storedFile = storedFileDao.findByIdValidated(userId, storedFileId);

            s3StorageClient.upload(storedFileId, ftpFile.getInputStream(), storedFile.getSize());
            ftpStorageClient.delete(storedFileId);

            storedFile.setStorage(Storage.S3);
            storedFileDao.save(storedFile);
        } catch (Exception e) {
            errorReporterService.report("Failed migrating StoredFile " + storedFileId, e);
        }
    }
}
