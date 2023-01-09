package com.github.saphyra.apphub.api.platform.storage.server;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface StorageEventController {
    @PostMapping(path = Endpoints.EVENT_DELETE_ACCOUNT)
    void deleteAccountEvent(@RequestBody SendEventRequest<DeleteAccountEvent> request);

    @PostMapping(Endpoints.EVENT_CLEAN_UP_STORED_FILES)
    void cleanUpStoredFiles();
}
