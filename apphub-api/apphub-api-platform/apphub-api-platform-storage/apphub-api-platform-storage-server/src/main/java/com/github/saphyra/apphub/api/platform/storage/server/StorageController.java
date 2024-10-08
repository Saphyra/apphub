package com.github.saphyra.apphub.api.platform.storage.server;

import com.github.saphyra.apphub.api.platform.storage.model.CreateFileRequest;
import com.github.saphyra.apphub.api.platform.storage.model.StoredFileResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.endpoints.StorageEndpoints;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.util.UUID;

public interface StorageController {
    /**
     * Creating a database record as a placeholder, what represents the new file to be uploaded
     */
    @PutMapping(StorageEndpoints.STORAGE_INTERNAL_CREATE_FILE)
    UUID createFile(@RequestBody CreateFileRequest request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    /**
     * Receiving the actual file data to the placeholder created before
     */
    @PutMapping(value = StorageEndpoints.STORAGE_UPLOAD_FILE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    void uploadFile(@PathVariable("storedFileId") UUID storedFileId, MultipartFile file, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader) throws IOException;

    @DeleteMapping(StorageEndpoints.STORAGE_INTERNAL_DELETE_FILE)
    void deleteFile(@PathVariable("storedFileId") UUID storedFileId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(StorageEndpoints.STORAGE_DOWNLOAD_FILE)
    ResponseEntity<StreamingResponseBody> downloadFile(@PathVariable("storedFileId") UUID storedFileId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(StorageEndpoints.STORAGE_GET_METADATA)
    StoredFileResponse getFileMetadata(@PathVariable("storedFileId") UUID storedFileId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
