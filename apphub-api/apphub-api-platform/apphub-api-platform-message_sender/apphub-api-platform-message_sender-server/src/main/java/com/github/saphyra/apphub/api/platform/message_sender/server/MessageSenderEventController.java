package com.github.saphyra.apphub.api.platform.message_sender.server;

import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.web.bind.annotation.PostMapping;

public interface MessageSenderEventController {
    @PostMapping(Endpoints.MESSAGE_SENDER_PING_REQUEST_EVENT)
    void sendPingRequests();

    @PostMapping(Endpoints.MESSAGE_SENDER_CONNECTION_CLEANUP_EVENT)
    void connectionCleanup();
}
