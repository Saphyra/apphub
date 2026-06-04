package com.github.saphyra.apphub.service.platform.storage.service;

import com.github.saphyra.apphub.api.platform.storage.model.CreateFileRequest;
import com.github.saphyra.apphub.api.platform.storage.model.StoredFileResponse;
import com.github.saphyra.apphub.api.platform.storage.server.StorageController;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.platform.storage.client.DownloadResult;
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
    private final CloneFileService cloneFileService;

    @Override
    public UUID createFile(CreateFileRequest request, AccessToken accessToken) {
        log.info("{} wants to create a file.", accessToken.getUserId());
        return storeFileService.createFile(accessToken.getUserId(), request.getFileName(), request.getSize());
    }

    @Override
    public void uploadFile(UUID storedFileId, MultipartFile file, AccessToken accessToken) throws IOException {
        storeFileService.uploadFile(accessToken.getUserId(), storedFileId, file.getInputStream(), file.getSize());
    }

    @Override
    public void deleteFile(UUID storedFileId, AccessToken accessToken) {
        log.info("{} wants to delete file {}", accessToken.getUserId(), storedFileId);
        deleteFileService.deleteFile(accessToken.getUserId(), storedFileId);
    }

    @Override
    public ResponseEntity<StreamingResponseBody> downloadFile(UUID storedFileId, AccessToken accessToken) {
        log.info("{} wants to query file {}", accessToken.getUserId(), storedFileId);
        BiWrapper<String, DownloadResult> result = downloadFileService.downloadFile(accessToken.getUserId(), storedFileId);

        StreamingResponseBody responseBody = outputStream -> {

            int numberOfBytesToWrite;
            byte[] data = new byte[4096];
            DownloadResult downloadResult = result.getEntity2();
            while ((numberOfBytesToWrite = downloadResult.getInputStream().read(data, 0, data.length)) != -1) {
                outputStream.write(data, 0, numberOfBytesToWrite);
            }

            downloadResult.close();
        };

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=%s", result.getEntity1()))
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(responseBody);
    }

    @Override
    public StoredFileResponse getFileMetadata(UUID storedFileId, AccessToken accessToken) {
        log.info("{} wants to know the metadata of file {}", accessToken.getUserId(), storedFileId);
        return metadataQueryService.getMetadata(accessToken.getUserId(), storedFileId);
    }

    @Override
    public UUID cloneFile(UUID storedFileId, AccessToken accessToken) {
        log.info("{} wants to clone StoredFile {}", accessToken.getUserId(), storedFileId);
        return cloneFileService.clone(accessToken.getUserId(), storedFileId);
    }
}
