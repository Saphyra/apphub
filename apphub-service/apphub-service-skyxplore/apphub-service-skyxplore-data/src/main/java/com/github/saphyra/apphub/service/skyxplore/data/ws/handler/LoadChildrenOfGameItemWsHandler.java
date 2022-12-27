package com.github.saphyra.apphub.service.skyxplore.data.ws.handler;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.skyxplore.ws.LoadChildrenOfGameItemRequest;
import com.github.saphyra.apphub.lib.skyxplore.ws.SkyXploreWsEvent;
import com.github.saphyra.apphub.lib.skyxplore.ws.SkyXploreWsEventName;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.LoadGameItemService;
import com.github.saphyra.apphub.service.skyxplore.data.ws.LoadGameWebSocketHandler;
import com.github.saphyra.apphub.service.skyxplore.data.ws.SkyXploreWsEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoadChildrenOfGameItemWsHandler implements SkyXploreWsEventHandler {
    private final ObjectMapperWrapper objectMapperWrapper;
    private final LoadGameItemService loadGameItemService;

    @Override
    public boolean canHandle(SkyXploreWsEventName eventName) {
        return eventName == SkyXploreWsEventName.LOAD_CHILDREN_OF_GAME_ITEM;
    }

    @Override
    public void handle(LoadGameWebSocketHandler webSocketHandler, WebSocketSession session, SkyXploreWsEvent event) {
        LoadChildrenOfGameItemRequest request = objectMapperWrapper.convertValue(event.getPayload(), LoadChildrenOfGameItemRequest.class);

        List<? extends GameItem> result = loadGameItemService.loadChildrenOfGameItem(request.getParent(), request.getType());

        SkyXploreWsEvent response = SkyXploreWsEvent.builder()
            .eventName(SkyXploreWsEventName.LOAD_CHILDREN_OF_GAME_ITEM)
            .id(event.getId())
            .payload(result)
            .build();

        webSocketHandler.sendEvent(session, response);
    }
}
