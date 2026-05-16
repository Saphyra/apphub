package com.github.saphyra.apphub.service.platform.storage.client;

import com.github.saphyra.apphub.service.platform.storage.dao.stored_file.Storage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StorageClientProvider {
    private final List<StorageClient> storageClients;

    public StorageClient getClientForType(Storage storage) {
        return storageClients.stream()
            .filter(storageClient -> storageClient.getType() == storage)
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("Unsupported storage type: " + storage));
    }
}
