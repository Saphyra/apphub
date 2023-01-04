package com.github.saphyra.apphub.service.platform.message_sender.config;

import com.github.saphyra.apphub.lib.config.common.Endpoints;
import com.github.saphyra.apphub.service.platform.message_sender.connection.AdminPanelErrorReportHandler;
import com.github.saphyra.apphub.service.platform.message_sender.connection.AdminPanelMonitoringWebSocketHandler;
import com.github.saphyra.apphub.service.platform.message_sender.connection.SkyXploreGameConnectionHandler;
import com.github.saphyra.apphub.service.platform.message_sender.connection.SkyXploreLobbyWebSocketHandler;
import com.github.saphyra.apphub.service.platform.message_sender.connection.SkyXploreMainMenuWebSocketHandler;
import com.github.saphyra.apphub.service.platform.message_sender.session.AuthenticationHandshakeHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@RequiredArgsConstructor
public class WebSocketConfiguration implements WebSocketConfigurer {
    private final AuthenticationHandshakeHandler authenticationHandshakeHandler;

    private final SkyXploreMainMenuWebSocketHandler skyXploreMainMenuWebSocketHandler;
    private final SkyXploreLobbyWebSocketHandler skyXploreLobbyWebSocketHandler;
    private final SkyXploreGameConnectionHandler skyXploreGameConnectionHandler;
    private final AdminPanelMonitoringWebSocketHandler adminPanelMonitoringWebSocketHandler;
    private final AdminPanelErrorReportHandler adminPanelErrorReportHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(skyXploreMainMenuWebSocketHandler, Endpoints.WS_CONNECTION_SKYXPLORE_MAIN_MENU)
            .addHandler(skyXploreLobbyWebSocketHandler, Endpoints.WS_CONNECTION_SKYXPLORE_LOBBY)
            .addHandler(skyXploreGameConnectionHandler, Endpoints.WS_CONNECTION_SKYXPLORE_GAME)
            .addHandler(adminPanelMonitoringWebSocketHandler, Endpoints.WS_CONNECTION_ADMIN_PANEL_MONITORING)
            .addHandler(adminPanelErrorReportHandler, Endpoints.WS_CONNECTION_ADMIN_PANEL_ERROR_REPORT)
            .setHandshakeHandler(authenticationHandshakeHandler);
    }
}
