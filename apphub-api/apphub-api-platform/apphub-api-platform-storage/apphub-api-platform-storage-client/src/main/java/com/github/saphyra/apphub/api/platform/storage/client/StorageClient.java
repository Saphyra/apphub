package com.github.saphyra.apphub.api.platform.storage.client;

import com.github.saphyra.apphub.api.platform.storage.model.CreateFileRequest;
import com.github.saphyra.apphub.api.platform.storage.model.StoredFileResponse;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.api.platform.storage.model.StorageEndpoints;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

import java.util.UUID;

public interface StorageClient {
    @PutExchange(StorageEndpoints.STORAGE_INTERNAL_CREATE_FILE)
    UUID createFile(@RequestBody CreateFileRequest request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) String accessTokenHeader);

    @DeleteExchange(StorageEndpoints.STORAGE_INTERNAL_DELETE_FILE)
    void deleteFile(@PathVariable("storedFileId") UUID storedFileId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) String accessTokenHeader);

    @GetExchange(StorageEndpoints.STORAGE_GET_METADATA)
    StoredFileResponse getFileMetadata(@PathVariable("storedFileId") UUID storedFileId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) String accessTokenHeader);

    @PostExchange(StorageEndpoints.STORAGE_INTERNAL_CLONE_FILE)
    UUID cloneFile(@PathVariable("storedFileId") UUID storedFileId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) String accessTokenHeader);
}
