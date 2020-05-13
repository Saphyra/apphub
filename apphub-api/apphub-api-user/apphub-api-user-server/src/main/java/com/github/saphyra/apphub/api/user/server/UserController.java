package com.github.saphyra.apphub.api.user.server;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface UserController {
    @RequestMapping(method = RequestMethod.POST, path = Endpoints.DELETE_ACCOUNT_EVENT)
    void deleteAccountEvent(SendEventRequest<DeleteAccountEvent> request);
}
