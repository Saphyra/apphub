package com.github.saphyra.apphub.service.platform.message_sender.connection;

import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import com.github.saphyra.apphub.api.skyxplore.lobby.client.SkyXploreLobbyWsApiClient;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@Slf4j
public class SkyXploreMainMenuWebSocketHandler extends DefaultWebSocketHandler {
    private final SkyXploreLobbyWsApiClient lobbyWsClient;
    private final CommonConfigProperties commonConfigProperties;

    public SkyXploreMainMenuWebSocketHandler(
        WebSocketHandlerContext context,
        SkyXploreLobbyWsApiClient lobbyWsClient,
        CommonConfigProperties commonConfigProperties
    ) {
        super(context);
        this.lobbyWsClient = lobbyWsClient;
        this.commonConfigProperties = commonConfigProperties;
    }

    @Override
    public MessageGroup getGroup() {
        return MessageGroup.SKYXPLORE_MAIN_MENU;
    }

    @Override
    protected void afterConnection(UUID userId) {
        lobbyWsClient.playerOnline(userId, commonConfigProperties.getDefaultLocale());
    }

    @Override
    protected void afterDisconnection(UUID userId) {
        lobbyWsClient.playerOffline(userId, commonConfigProperties.getDefaultLocale());
    }
}
