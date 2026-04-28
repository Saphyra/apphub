package com.github.saphyra.apphub.service.platform.storage.client.ftp;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.platform.storage.client.DownloadResult;
import com.github.saphyra.apphub.service.platform.storage.dao.Storage;
import com.github.saphyra.apphub.service.platform.storage.client.StorageClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@FtpClientEnabled
class FtpStorageClient implements StorageClient {
    private final FtpClientFactory ftpClientFactory;
    private final UuidConverter uuidConverter;
    private final ErrorReporterService errorReporterService;

    @Override
    public Storage getType() {
        return Storage.FTP;
    }

    @Override
    public DownloadResult download(UUID storedFileId) {
        FtpClientWrapper ftpClient = ftpClientFactory.create();

        try {
            InputStream inputStream = ftpClient.downloadFile(uuidConverter.convertDomain(storedFileId));
            return DownloadResult.builder()
                .inputStream(inputStream)
                .ftpClient(ftpClient)
                .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void upload(UUID storedFileId, InputStream file, long fileSize) {
        try (FtpClientWrapper ftpClient = ftpClientFactory.create()) {
            ftpClient.storeFile(uuidConverter.convertDomain(storedFileId), file);
        }
    }

    @Override
    public void delete(UUID storedFileId) {
        try (FtpClientWrapper ftpClient = ftpClientFactory.create()) {
            ftpClient.deleteFile(uuidConverter.convertDomain(storedFileId));
        } catch (Exception e) {
            errorReporterService.report("Failed deleting FTP file " + storedFileId, e);
        }
    }
}
