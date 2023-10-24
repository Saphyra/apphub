package com.github.saphyra.apphub.service.admin_panel.ws;

import com.github.saphyra.apphub.lib.config.common.Endpoints;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.web_socket.core.handler.AbstractWebSocketHandler;
import com.github.saphyra.apphub.lib.web_socket.core.handler.WebSocketHandlerContext;
import org.springframework.stereotype.Component;

@Component
public class ErrorReportWebSocketHandler extends AbstractWebSocketHandler {
    public ErrorReportWebSocketHandler(WebSocketHandlerContext context) {
        super(context);
    }

    @Override
    public String getEndpoint() {
        return Endpoints.WS_CONNECTION_ADMIN_PANEL_ERROR_REPORT;
    }

    public void sendToAllConnectedClient(WebSocketEvent event) {
        sessions.values()
            .forEach(webSocketSessionWrapper -> sendEventToSession(webSocketSessionWrapper, event));
    }
}
