package com.github.saphyra.apphub.service.platform.message_sender.connection;

import com.github.saphyra.apphub.api.admin_panel.client.AdminPanelWsClient;
import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
//TODO unit test
public class AdminPanelMonitoringWebSocketHandler extends DefaultWebSocketHandler {
    private final CommonConfigProperties commonConfigProperties;
    private final AdminPanelWsClient adminPanelWsClient;

    public AdminPanelMonitoringWebSocketHandler(WebSocketHandlerContext context, CommonConfigProperties commonConfigProperties, AdminPanelWsClient adminPanelWsClient) {
        super(context);
        this.commonConfigProperties = commonConfigProperties;
        this.adminPanelWsClient = adminPanelWsClient;
    }

    @Override
    public MessageGroup getGroup() {
        return MessageGroup.ADMIN_PANEL_MONITORING;
    }

    @Override
    protected void afterConnection(UUID userId) {
        adminPanelWsClient.userConnected(userId, commonConfigProperties.getDefaultLocale());
    }

    @Override
    protected void afterDisconnection(UUID userId) {
        adminPanelWsClient.userDisconnected(userId, commonConfigProperties.getDefaultLocale());
    }

    @Override
    protected void handleMessage(UUID userId, WebSocketEvent event) {
        log.info("Event from {}: {}", userId, event.getEventName());
        adminPanelWsClient.processWebSocketEvent(userId, event, commonConfigProperties.getDefaultLocale());
    }

    @Override
    public void handleExpiredConnections(List<UUID> disconnectedUsers) {
        disconnectedUsers.forEach(this::afterDisconnection);
    }
}
