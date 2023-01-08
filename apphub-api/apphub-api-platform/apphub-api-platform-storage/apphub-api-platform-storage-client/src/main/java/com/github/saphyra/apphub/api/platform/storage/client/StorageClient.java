package com.github.saphyra.apphub.api.platform.storage.client;

import com.github.saphyra.apphub.api.platform.storage.model.CreateFileRequest;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient("storage")
public interface StorageClient {
    @PutMapping(Endpoints.STORAGE_INTERNAL_CREATE_FILE)
    UUID createFile(@RequestBody CreateFileRequest request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) String accessTokenHeader, @RequestHeader(Constants.LOCALE_HEADER) String locale);

    @DeleteMapping(Endpoints.STORAGE_INTERNAL_DELETE_FILE)
    void deleteFile(@PathVariable("storedFileId") UUID storedFileId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) String accessTokenHeader, @RequestHeader(Constants.LOCALE_HEADER) String locale);

    @PostMapping(Endpoints.STORAGE_INTERNAL_DUPLICATE_FILE)
    UUID duplicateFile(@PathVariable("storedFileId") UUID storedFileId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) String accessTokenHeader, @RequestHeader(Constants.LOCALE_HEADER) String locale);
}
