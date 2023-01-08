package com.github.saphyra.apphub.service.notebook.service;

import com.github.saphyra.apphub.api.platform.storage.client.StorageClient;
import com.github.saphyra.apphub.api.platform.storage.model.CreateFileRequest;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class StorageProxy {
    private final StorageClient storageClient;
    private final AccessTokenProvider accessTokenProvider;
    private final LocaleProvider localeProvider;

    public UUID createFile(String fileName, String extension, Long size) {
        CreateFileRequest request = CreateFileRequest.builder()
            .fileName(fileName)
            .extension(extension)
            .size(size)
            .build();
        return storageClient.createFile(request, accessTokenProvider.getAsString(), localeProvider.getLocaleValidated());
    }

    public void deleteFile(UUID fileId) {
        storageClient.deleteFile(fileId, accessTokenProvider.getAsString(), localeProvider.getLocaleValidated());
    }

    public UUID duplicateFile(UUID fileId) {
        return storageClient.duplicateFile(fileId, accessTokenProvider.getAsString(), localeProvider.getLocaleValidated());
    }
}
