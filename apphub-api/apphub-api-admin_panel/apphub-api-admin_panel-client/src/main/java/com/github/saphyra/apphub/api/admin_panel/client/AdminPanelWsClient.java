package com.github.saphyra.apphub.api.admin_panel.client;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient("admin-panel-ws-client")
public interface AdminPanelWsClient {
    @PutMapping(Endpoints.ADMIN_PANEL_WEB_SOCKET_USER_CONNECTED)
    void userConnected(@PathVariable("userId") UUID userId, @RequestHeader(Constants.LOCALE_HEADER) String locale);

    @DeleteMapping(Endpoints.ADMIN_PANEL_WEB_SOCKET_USER_DISCONNECTED)
    void userDisconnected(@PathVariable("userId") UUID userId, @RequestHeader(Constants.LOCALE_HEADER) String locale);

    @PostMapping(Endpoints.ADMIN_PANEL_INTERNAL_LOBBY_PROCESS_WEB_SOCKET_EVENTS)
    void processWebSocketEvent(@PathVariable("userId") UUID userId, @RequestBody WebSocketEvent event, @RequestHeader(Constants.LOCALE_HEADER) String locale);
}
