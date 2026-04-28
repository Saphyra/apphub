package com.github.saphyra.apphub.service.platform.storage.client.ftp;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.platform.storage.client.StorageCleaner;
import com.github.saphyra.apphub.service.platform.storage.dao.Storage;
import com.github.saphyra.apphub.service.platform.storage.dao.StoredFileDao;
import com.github.saphyra.apphub.service.platform.storage.dao.StoredFileView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@FtpClientEnabled
@RequiredArgsConstructor
@Slf4j
class FtpStorageCleaner implements StorageCleaner {
    private final StoredFileDao storedFileDao;
    private final FtpClientFactory ftpClientFactory;
    private final UuidConverter uuidConverter;

    @Override
    public void cleanup() {
        log.info("File cleanup triggered.");

        try (FtpClientWrapper client = ftpClientFactory.create()) {
            List<StoredFileView> storedFiles = storedFileDao.getViewsByStorage(Storage.FTP);
            List<String> storedFileNames = storedFiles.stream()
                .filter(StoredFileView::isFileUploaded)
                .map(StoredFileView::getStoredFileId)
                .toList();
            List<FTPFile> files = client.listFiles("/");

            List<String> fileNames = files.stream()
                .map(FTPFile::getName)
                .toList();

            List<String> filesToDelete = fileNames.stream()
                .filter(fileName -> !storedFileNames.contains(fileName))
                .toList();

            List<UUID> recordsToDelete = storedFileNames.stream()
                .filter(fileName -> !fileNames.contains(fileName))
                .map(uuidConverter::convertEntity)
                .toList();

            log.info("Deleting files {}", filesToDelete);
            filesToDelete.forEach(client::deleteFile);

            log.info("Deleting StoredFiles {}", recordsToDelete);
            storedFileDao.deleteByStorageAndIds(Storage.FTP, recordsToDelete);
        }

        log.info("File cleanup finished.");
    }
}
