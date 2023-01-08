package com.github.saphyra.apphub.service.platform.storage.service;

import com.github.saphyra.apphub.api.platform.storage.model.CreateFileRequest;
import com.github.saphyra.apphub.api.platform.storage.server.StorageController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.platform.storage.service.store.StoreFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

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
        return storeFileService.createFile(accessTokenHeader.getUserId(), request.getFileName(), request.getExtension(), request.getSize());
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
    public UUID duplicateFile(UUID storedFileId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to duplicate file {}", accessTokenHeader.getUserId(), storedFileId);
        return duplicateFileService.duplicateFile(accessTokenHeader.getUserId(), storedFileId);
    }
}
