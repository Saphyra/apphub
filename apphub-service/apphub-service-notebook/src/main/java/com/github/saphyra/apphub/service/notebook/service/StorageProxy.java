package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.api.platform.storage.client.StorageClient;
import com.github.saphyra.apphub.api.platform.storage.model.CreateFileRequest;
import com.github.saphyra.apphub.api.platform.storage.model.StoredFileResponse;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class StorageProxy {
    private final StorageClient storageClient;
    private final AccessTokenProvider accessTokenProvider;
    private final LocaleProvider localeProvider;

    public UUID createFile(String fileName, Long size) {
        CreateFileRequest request = CreateFileRequest.builder()
            .fileName(fileName)
            .size(size)
            .build();
        return storageClient.createFile(request, accessTokenProvider.getAsString(), localeProvider.getLocaleValidated());
    }

    public void deleteFile(UUID fileId) {
        storageClient.deleteFile(fileId, accessTokenProvider.getAsString(), localeProvider.getLocaleValidated());
    }

    public StoredFileResponse getFileMetadata(UUID storedFileId) {
        try {
            return storageClient.getFileMetadata(storedFileId, accessTokenProvider.getAsString(), localeProvider.getLocaleValidated());
        } catch (FeignException e) {
            if (e.status() == HttpStatus.NOT_FOUND.value()) {
                log.warn("StoredFile not found with id {}", storedFileId);
                return StoredFileResponse.builder()
                    .fileUploaded(false)
                    .build();
            }

            throw e;
        }
    }
}
