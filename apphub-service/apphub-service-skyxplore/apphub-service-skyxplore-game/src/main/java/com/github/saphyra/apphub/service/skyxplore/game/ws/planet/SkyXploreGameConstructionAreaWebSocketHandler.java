package com.github.saphyra.apphub.service.skyxplore.game.ws.planet;

import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.config.common.endpoints.skyxplore.GenericSkyXploreEndpoints;
import com.github.saphyra.apphub.lib.web_socket.core.handler.AbstractWebSocketHandler;
import com.github.saphyra.apphub.lib.web_socket.core.handler.WebSocketHandlerContext;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.ws.etc.WsSessionConstructionAreaIdMapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class SkyXploreGameConstructionAreaWebSocketHandler extends AbstractWebSocketHandler {
    private final UuidConverter uuidConverter;
    private final GameDao gameDao;

    private final Map<String, UUID> openedConstructionAreas = new ConcurrentHashMap<>();

    public SkyXploreGameConstructionAreaWebSocketHandler(WebSocketHandlerContext context, UuidConverter uuidConverter, GameDao gameDao) {
        super(context);
        this.uuidConverter = uuidConverter;
        this.gameDao = gameDao;
    }

    @Override
    public String getEndpoint() {
        return GenericSkyXploreEndpoints.WS_CONNECTION_SKYXPLORE_GAME_CONSTRUCTION_AREA;
    }

    @Override
    protected void afterDisconnection(UUID userId, String sessionId) {
        openedConstructionAreas.remove(sessionId);
    }

    @Override
    protected void handleMessage(UUID userId, WebSocketEvent event, String sessionId) {
        if (event.getEventName() == WebSocketEventName.PING) {
            log.info("Ping arrived from {} to {}", userId, getEndpoint());
        } else if (event.getEventName() == WebSocketEventName.SKYXPLORE_GAME_CONSTRUCTION_AREA_OPENED) {
            UUID constructionAreaId = uuidConverter.convertEntity(event.getPayload().toString());
            log.info("{} opened constructionArea {}", userId, constructionAreaId);

            gameDao.findByUserId(userId)
                .ifPresent(game -> openedConstructionAreas.put(sessionId, constructionAreaId));
        }
    }

    public List<WsSessionConstructionAreaIdMapping> getConnectedUsers() {
        return openedConstructionAreas.entrySet()
            .stream()
            .map(entry -> WsSessionConstructionAreaIdMapping.builder()
                .sessionId(entry.getKey())
                .constructionAreaId(entry.getValue())
                .userId(getUserId(entry.getKey()))
                .build())
            .toList();
    }
}
