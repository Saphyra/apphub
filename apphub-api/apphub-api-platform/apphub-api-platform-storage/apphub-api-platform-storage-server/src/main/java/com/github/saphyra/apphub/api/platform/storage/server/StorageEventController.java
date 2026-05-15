package com.github.saphyra.apphub.api.platform.storage.server;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.config.common.GenericEndpoints;
import com.github.saphyra.apphub.api.platform.storage.model.StorageEndpoints;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface StorageEventController {
    @PostMapping(path = GenericEndpoints.EVENT_DELETE_ACCOUNT)
    void deleteAccountEvent(@RequestBody SendEventRequest<DeleteAccountEvent> request);
}
