package com.github.saphyra.apphub.service.skyxplore.game.ws.planet;

import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.config.common.endpoints.skyxplore.GenericSkyXploreEndpoints;
import com.github.saphyra.apphub.lib.web_socket.core.handler.AbstractWebSocketHandler;
import com.github.saphyra.apphub.lib.web_socket.core.handler.WebSocketHandlerContext;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.ws.etc.WsSessionPlanetIdMapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class SkyXploreGamePlanetWebSocketHandler extends AbstractWebSocketHandler {
    private final UuidConverter uuidConverter;
    private final GameDao gameDao;

    private final Map<String, UUID> openedPlanetIds = new ConcurrentHashMap<>();

    public SkyXploreGamePlanetWebSocketHandler(WebSocketHandlerContext context, GameDao gameDao, UuidConverter uuidConverter) {
        super(context);
        this.uuidConverter = uuidConverter;
        this.gameDao = gameDao;
    }

    @Override
    public String getEndpoint() {
        return GenericSkyXploreEndpoints.WS_CONNECTION_SKYXPLORE_GAME_PLANET;
    }

    @Override
    protected void afterDisconnection(UUID userId, String sessionId) {
        openedPlanetIds.remove(sessionId);
    }

    @Override
    protected void handleMessage(UUID userId, WebSocketEvent event, String sessionId) {
        if (event.getEventName() == WebSocketEventName.PING) {
            log.info("Ping arrived from {} to {}", userId, getEndpoint());
        } else if (event.getEventName() == WebSocketEventName.SKYXPLORE_GAME_PLANET_OPENED) {
            UUID planetId = uuidConverter.convertEntity(event.getPayload().toString());
            log.info("{} opened planet {}", userId, planetId);

            gameDao.findByUserId(userId)
                .ifPresent(game -> openedPlanetIds.put(sessionId, planetId));
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
