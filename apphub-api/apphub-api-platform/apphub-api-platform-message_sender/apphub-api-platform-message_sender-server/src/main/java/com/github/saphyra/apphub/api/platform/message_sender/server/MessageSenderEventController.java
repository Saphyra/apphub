package com.github.saphyra.apphub.api.platform.message_sender.server;

import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.web.bind.annotation.PostMapping;

public interface MessageSenderEventController {
    /**
     * Event triggered by scheduler-service.
     * Pinging all the connected users, to check if the connection is still alive
     */
    @PostMapping(Endpoints.EVENT_MESSAGE_SENDER_PING_REQUEST)
    void sendPingRequests();

    /**
     * Event triggered by scheduler-service.
     * Removing dead connections
     */
    @PostMapping(Endpoints.EVENT_MESSAGE_SENDER_CONNECTION_CLEANUP)
    void connectionCleanup();
}
