package com.github.saphyra.apphub.service.platform.storage.client;

import com.github.saphyra.apphub.service.platform.storage.dao.Storage;

import java.io.InputStream;
import java.util.UUID;

public interface StorageClient {
    Storage getType();

    DownloadResult download(UUID storedFileId);

    void upload(UUID storedFileId, InputStream file, long fileSize);

    void delete(UUID storedFileId);
}
