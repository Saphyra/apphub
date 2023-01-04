package com.github.saphyra.apphub.api.admin_panel.server;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

public interface AdminPanelWsController {
    @PutMapping(Endpoints.ADMIN_PANEL_WEB_SOCKET_USER_CONNECTED)
    void userConnected(@PathVariable("userId") UUID userId);

    @DeleteMapping(Endpoints.ADMIN_PANEL_WEB_SOCKET_USER_DISCONNECTED)
    void userDisconnected(@PathVariable("userId") UUID userId);

    @PostMapping(Endpoints.ADMIN_PANEL_INTERNAL_LOBBY_PROCESS_WEB_SOCKET_EVENTS)
    void processWebSocketEvent(@PathVariable("userId") UUID userId, @RequestBody WebSocketEvent event);
}
