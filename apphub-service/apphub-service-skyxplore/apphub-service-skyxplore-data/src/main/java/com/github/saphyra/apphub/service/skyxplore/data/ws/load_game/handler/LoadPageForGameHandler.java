package com.github.saphyra.apphub.service.skyxplore.data.ws.load_game.handler;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.lib.skyxplore.ws.LoadPageForGameRequest;
import com.github.saphyra.apphub.lib.skyxplore.ws.SkyXploreWsEvent;
import com.github.saphyra.apphub.lib.skyxplore.ws.SkyXploreWsEventName;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.LoadGameItemService;
import com.github.saphyra.apphub.service.skyxplore.data.ws.load_game.LoadGameWebSocketHandler;
import com.github.saphyra.apphub.service.skyxplore.data.ws.load_game.SkyXploreLoadGameWsEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoadPageForGameHandler implements SkyXploreLoadGameWsEventHandler {
    private final ObjectMapper objectMapper;
    private final LoadGameItemService loadGameItemService;

    @Override
    public boolean canHandle(SkyXploreWsEventName eventName) {
        return SkyXploreWsEventName.LOAD_PAGE_FOR_GAME == eventName;
    }

    @Override
    public void handle(LoadGameWebSocketHandler webSocketHandler, WebSocketSession session, SkyXploreWsEvent event) {
        log.info("LoadPageForGameRequest arrived: {}", event);

        LoadPageForGameRequest request = objectMapper.convertValue(event.getPayload(), LoadPageForGameRequest.class);

        List<? extends GameItem> result = loadGameItemService.loadPageForGameItems(request.getGameId(), request.getPage(), request.getType());

        SkyXploreWsEvent response = SkyXploreWsEvent.builder()
            .eventName(SkyXploreWsEventName.LOAD_PAGE_FOR_GAME)
            .id(event.getId())
            .payload(result)
            .build();

        webSocketHandler.sendEvent(session, response);
    }
}
