package com.github.saphyra.apphub.service.platform.storage.service;

import com.github.saphyra.apphub.api.platform.storage.model.CreateFileRequest;
import com.github.saphyra.apphub.api.platform.storage.model.StoredFileResponse;
import com.github.saphyra.apphub.api.platform.storage.server.StorageController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.platform.storage.service.store.StoreFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StorageControllerImpl implements StorageController {
    private final StoreFileService storeFileService;
    private final DownloadFileService downloadFileService;
    private final DeleteFileService deleteFileService;
    private final StoredFileMetadataQueryService metadataQueryService;

    @Override
    public UUID createFile(CreateFileRequest request, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to create a file.", accessTokenHeader.getUserId());
        return storeFileService.createFile(accessTokenHeader.getUserId(), request.getFileName(), request.getSize());
    }

    @Override
    public void uploadFile(UUID storedFileId, MultipartFile file, AccessTokenHeader accessTokenHeader) throws IOException {
        storeFileService.uploadFile(accessTokenHeader.getUserId(), storedFileId, file.getInputStream(), file.getSize());
    }

    @Override
    public void deleteFile(UUID storedFileId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to delete file {}", accessTokenHeader.getUserId(), storedFileId);
        deleteFileService.deleteFile(accessTokenHeader.getUserId(), storedFileId);
    }

    @Override
    public ResponseEntity<StreamingResponseBody> downloadFile(UUID storedFileId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to query file {}", accessTokenHeader.getUserId(), storedFileId);
        DownloadResult result = downloadFileService.downloadFile(accessTokenHeader.getUserId(), storedFileId);

        StreamingResponseBody responseBody = outputStream -> {

            int numberOfBytesToWrite;
            byte[] data = new byte[4096];
            while ((numberOfBytesToWrite = result.getInputStream().read(data, 0, data.length)) != -1) {
                outputStream.write(data, 0, numberOfBytesToWrite);
            }

            result.getInputStream().close();
            result.getFtpClient().close();
        };

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=%s", result.getStoredFile().getFileName()))
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(responseBody);
    }

    @Override
    public StoredFileResponse getFileMetadata(UUID storedFileId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know the metadata of file {}", accessTokenHeader.getUserId(), storedFileId);
        return metadataQueryService.getMetadata(accessTokenHeader.getUserId(), storedFileId);
    }
}
