package com.github.saphyra.apphub.service.platform.message_sender.connection;

import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import com.github.saphyra.apphub.lib.web_socket.core.domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.web_socket.core.domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class AdminPanelErrorReportHandler extends DefaultWebSocketHandler {
    public AdminPanelErrorReportHandler(WebSocketHandlerContext context) {
        super(context);
    }

    @Override
    public MessageGroup getGroup() {
        return MessageGroup.ADMIN_PANEL_ERROR_REPORT;
    }

    @Override
    protected void handleMessage(UUID userId, WebSocketEvent event) {
        if (event.getEventName() != WebSocketEventName.PING) {
            throw ExceptionFactory.forbiddenOperation(getGroup() + " cannot handle incoming messages.");
        }
    }
}
