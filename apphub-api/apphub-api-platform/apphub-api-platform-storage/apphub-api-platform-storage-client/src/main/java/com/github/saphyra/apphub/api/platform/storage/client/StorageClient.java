package com.github.saphyra.apphub.api.platform.storage.client;

import com.github.saphyra.apphub.api.platform.storage.model.CreateFileRequest;
import com.github.saphyra.apphub.api.platform.storage.model.StoredFileResponse;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.api.platform.storage.model.StorageEndpoints;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "storage", url = "${serviceHosts.storage}")
public interface StorageClient {
    @PutMapping(StorageEndpoints.STORAGE_INTERNAL_CREATE_FILE)
    UUID createFile(@RequestBody CreateFileRequest request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) String accessTokenHeader);

    @DeleteMapping(StorageEndpoints.STORAGE_INTERNAL_DELETE_FILE)
    void deleteFile(@PathVariable("storedFileId") UUID storedFileId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) String accessTokenHeader);

    @GetMapping(StorageEndpoints.STORAGE_GET_METADATA)
    StoredFileResponse getFileMetadata(@PathVariable("storedFileId") UUID storedFileId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) String accessTokenHeader);

    @PostMapping(StorageEndpoints.STORAGE_INTERNAL_CLONE_FILE)
    UUID cloneFile(@PathVariable("storedFileId") UUID storedFileId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) String accessTokenHeader);
}
