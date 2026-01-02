package com.github.saphyra.apphub.api.platform.storage.server;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.config.common.endpoints.GenericEndpoints;
import com.github.saphyra.apphub.lib.config.common.endpoints.StorageEndpoints;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface StorageEventController {
    @PostMapping(path = GenericEndpoints.EVENT_DELETE_ACCOUNT)
    void deleteAccountEvent(@RequestBody SendEventRequest<DeleteAccountEvent> request);

    /**
     * Evicting records from the database what has no file uploaded for a long time
     */
    @PostMapping(StorageEndpoints.EVENT_CLEAN_UP_STORED_FILES)
    void cleanUpStoredFiles();

    /**
     * Deleting files from FTP server that has no corresponding database record and vice versa
     */
    @PostMapping(StorageEndpoints.EVENT_FILE_CLEANUP)
    void cleanupFiles();
}
