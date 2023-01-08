package com.github.saphyra.apphub.service.platform.storage.service;

import com.github.saphyra.apphub.api.platform.storage.model.CreateFileRequest;
import com.github.saphyra.apphub.api.platform.storage.server.StorageController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.platform.storage.dao.StoredFile;
import com.github.saphyra.apphub.service.platform.storage.service.store.StoreFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class StorageControllerImpl implements StorageController {
    private final StoreFileService storeFileService;
    private final DownloadFileService downloadFileService;
    private final DeleteFileService deleteFileService;
    private final DuplicateFileService duplicateFileService;

    @Override
    public UUID createFile(CreateFileRequest request, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to create a file.", accessTokenHeader.getUserId());
        return storeFileService.createFile(accessTokenHeader.getUserId(), request.getExtension(), request.getSize());
    }

    @Override
    public UUID uploadFile(UUID storedFileId, HttpServletRequest request, AccessTokenHeader accessTokenHeader) throws IOException, FileUploadException {
        ServletFileUpload upload = new ServletFileUpload();
        FileItemIterator iterator = upload.getItemIterator(request);
        while (iterator.hasNext()) {
            FileItemStream item = iterator.next();

            if (!item.isFormField()) {
                InputStream inputStream = item.openStream();
                return storeFileService.uploadFile(accessTokenHeader.getUserId(), storedFileId, inputStream);
            }
        }

        throw new RuntimeException("Failed processing request");
    }

    @Override
    public void deleteFile(UUID storedFileId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to delete file {}", accessTokenHeader.getUserId(), storedFileId);
        deleteFileService.deleteFile(accessTokenHeader.getUserId(), storedFileId);
    }

    @Override
    public ResponseEntity<InputStreamResource> downloadFile(UUID storedFileId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to query file {}", accessTokenHeader.getUserId(), storedFileId);
        BiWrapper<InputStream, StoredFile> result = downloadFileService.downloadFile(accessTokenHeader.getUserId(), storedFileId);

        return ResponseEntity.ok()
            .contentLength(result.getEntity2().getSize())
            .contentType(resolveContentType(result.getEntity2().getExtension()))
            .body(new InputStreamResource(result.getEntity1()));
    }

    private MediaType resolveContentType(String extension) {
        switch (extension) {
            case "png":
                return MediaType.IMAGE_PNG;
            case "jpg":
            case "jpeg":
                return MediaType.IMAGE_JPEG;
            default:
                return MediaType.ALL;
        }
    }

    @Override
    public UUID duplicateFile(UUID storedFileId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to duplicate file {}", accessTokenHeader.getUserId(), storedFileId);
        return duplicateFileService.duplicateFile(accessTokenHeader.getUserId(), storedFileId);
    }
}
