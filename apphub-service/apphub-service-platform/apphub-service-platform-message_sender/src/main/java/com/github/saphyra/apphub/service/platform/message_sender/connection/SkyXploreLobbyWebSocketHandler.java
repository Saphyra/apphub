package com.github.saphyra.apphub.service.platform.message_sender.connection;

import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.skyxplore.lobby.client.SkyXploreLobbyApiClient;
import com.github.saphyra.apphub.api.skyxplore.lobby.client.SkyXploreLobbyWsApiClient;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.config.access_token.AccessTokenHeaderConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Controller
@Slf4j
public class SkyXploreLobbyWebSocketHandler extends DefaultWebSocketHandler {
    private final AccessTokenHeaderConverter accessTokenHeaderConverter;
    private final SkyXploreLobbyApiClient lobbyClient;
    private final SkyXploreLobbyWsApiClient lobbyWsClient;
    private final CommonConfigProperties commonConfigProperties;

    public SkyXploreLobbyWebSocketHandler(
        WebSocketHandlerContext context,
        AccessTokenHeaderConverter accessTokenHeaderConverter,
        SkyXploreLobbyApiClient lobbyClient,
        SkyXploreLobbyWsApiClient lobbyWsClient,
        CommonConfigProperties commonConfigProperties
    ) {
        super(context);
        this.accessTokenHeaderConverter = accessTokenHeaderConverter;
        this.lobbyClient = lobbyClient;
        this.lobbyWsClient = lobbyWsClient;
        this.commonConfigProperties = commonConfigProperties;
    }

    @Override
    public MessageGroup getGroup() {
        return MessageGroup.SKYXPLORE_LOBBY;
    }

    @Override
    protected void afterConnection(UUID userId) {
        lobbyClient.userJoinedToLobby(userId, commonConfigProperties.getDefaultLocale());
    }

    @Override
    protected void afterDisconnection(UUID userId) {
        lobbyClient.userLeftLobby(userId, commonConfigProperties.getDefaultLocale());
    }

    @Override
    protected void handleMessage(UUID userId, WebSocketEvent event) {
        log.info("Event from {}: {}", userId, event.getEventName());
        lobbyWsClient.processWebSocketEvent(userId, event, commonConfigProperties.getDefaultLocale());
    }

    @Override
    public void handleExpiredConnections(List<UUID> disconnectedUsers) {
        disconnectedUsers.forEach(this::sendExitFromLobbyRequest);
    }

    private void sendExitFromLobbyRequest(UUID userId) {
        try {
            AccessTokenHeader accessTokenHeader = AccessTokenHeader.builder()
                .userId(userId)
                .roles(Arrays.asList("SKYXPLORE", "ACCESS"))
                .build();
            String accessTokenHeaderString = accessTokenHeaderConverter.convertDomain(accessTokenHeader);
            lobbyClient.exitFromLobby(accessTokenHeaderString, commonConfigProperties.getDefaultLocale());
        } catch (Exception e) {
            log.error("Failed exiting user {} from lobby.", userId, e);
        }
    }
}
