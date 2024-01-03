package com.github.saphyra.apphub.service.skyxplore.game.ws.population;

import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import com.github.saphyra.apphub.lib.web_socket.core.handler.AbstractWebSocketHandler;
import com.github.saphyra.apphub.lib.web_socket.core.handler.WebSocketHandlerContext;
import com.github.saphyra.apphub.service.skyxplore.game.ws.etc.WsSessionPlanetIdMapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
//TODO unit test
public class SkyXploreGamePopulationWebSocketHandler extends AbstractWebSocketHandler {
    private final UuidConverter uuidConverter;

    private final Map<String, UUID> openedPlanetIds = new ConcurrentHashMap<>();

    public SkyXploreGamePopulationWebSocketHandler(WebSocketHandlerContext context) {
        super(context);
        this.uuidConverter = context.getUuidConverter();
    }

    @Override
    public String getEndpoint() {
        return Endpoints.WS_CONNECTION_SKYXPLORE_GAME_POPULATION;
    }

    @Override
    protected void afterDisconnection(UUID userId, String sessionId) {
        openedPlanetIds.remove(sessionId);
    }

    protected void handleMessage(UUID userId, WebSocketEvent event, String sessionId) {
        if (event.getEventName() == WebSocketEventName.PING) {
            log.info("Ping arrived from {} to {}", userId, getEndpoint());
        } else if (event.getEventName() == WebSocketEventName.SKYXPLORE_GAME_POPULATION_OPENED) {
            UUID planetId = uuidConverter.convertEntity(event.getPayload().toString());
            log.info("{} opened population of planet {}", userId, planetId);

            openedPlanetIds.put(sessionId, planetId);
        }
    }

    public List<WsSessionPlanetIdMapping> getConnectedUsers() {
        return openedPlanetIds.entrySet()
            .stream()
            .map(entry -> WsSessionPlanetIdMapping.builder()
                .sessionId(entry.getKey())
                .planetId(entry.getValue())
                .userId(getUserId(entry.getKey()))
                .build())
            .toList();
    }
}
