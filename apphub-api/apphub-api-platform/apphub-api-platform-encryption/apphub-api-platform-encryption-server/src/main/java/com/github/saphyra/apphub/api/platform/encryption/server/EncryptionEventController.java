package com.github.saphyra.apphub.api.platform.encryption.server;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface EncryptionEventController {
    @PostMapping(path = Endpoints.EVENT_DELETE_ACCOUNT)
        //TODO integration test
    void deleteAccountEvent(@RequestBody SendEventRequest<DeleteAccountEvent> request);
}
