package com.github.saphyra.apphub.service.platform.message_sender.connection;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import com.github.saphyra.apphub.api.skyxplore.lobby.client.SkyXploreLobbyApiClient;
import com.github.saphyra.apphub.lib.config.CommonConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@Slf4j
//TODO unit test
public class SkyXploreLobbyWebSocketHandler extends DefaultWebSocketHandler {
    private final SkyXploreLobbyApiClient lobbyClient;
    private final CommonConfigProperties commonConfigProperties;

    public SkyXploreLobbyWebSocketHandler(WebSocketHandlerContext context, SkyXploreLobbyApiClient lobbyClient, CommonConfigProperties commonConfigProperties) {
        super(context);
        this.lobbyClient = lobbyClient;
        this.commonConfigProperties = commonConfigProperties;
    }

    @Override
    public MessageGroup getGroup() {
        return MessageGroup.SKYXPLORE_LOBBY;
    }

    @Override
    protected void handleMessage(UUID userId, WebSocketEvent event) {
        log.info("Event from {}: {}", userId, event.getEventName());
        lobbyClient.processWebSocketEvent(userId, event, commonConfigProperties.getDefaultLocale());
    }
}
