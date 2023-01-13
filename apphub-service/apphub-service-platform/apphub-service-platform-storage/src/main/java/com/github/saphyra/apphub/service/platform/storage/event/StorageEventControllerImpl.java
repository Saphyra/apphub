package com.github.saphyra.apphub.service.platform.storage.event;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.api.platform.storage.server.StorageEventController;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StorageEventControllerImpl implements StorageEventController {
    private final DeleteAccountEventProcessor deleteAccountEventProcessor;
    private final StoredFileCleanupEventProcessor storedFileCleanupEventProcessor;

    @Override
    public void deleteAccountEvent(SendEventRequest<DeleteAccountEvent> request) {
        log.info("Deleting files for user {}", request.getPayload().getUserId());
        deleteAccountEventProcessor.deleteUserData(request.getPayload().getUserId());
    }

    @Override
    public void cleanUpStoredFiles() {
        log.info("Cleaning up StoredFiles...");
        storedFileCleanupEventProcessor.cleanup();
    }
}
