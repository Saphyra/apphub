package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.skyxplore.ws.LoadChildrenOfGameItemRequest;
import com.github.saphyra.apphub.lib.skyxplore.ws.LoadGameItemRequest;
import com.github.saphyra.apphub.lib.skyxplore.ws.SkyXploreWsEvent;
import com.github.saphyra.apphub.lib.skyxplore.ws.SkyXploreWsEventName;
import com.github.saphyra.apphub.service.skyxplore.game.common.ws.SkyXploreWsClient;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.ws.WebSocketClientCache;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Component
@Slf4j
public class GameItemLoader {
    private final ObjectMapperWrapper objectMapperWrapper;
    private final WebSocketClientCache wsClientCache;
    private final IdGenerator idGenerator;

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public <T extends GameItem> Optional<T> loadItem(UUID id, GameItemType type) {
        SkyXploreWsClient wsClient = wsClientCache.borrowObject();

        try {
            LoadGameItemRequest request = LoadGameItemRequest.builder()
                .id(id)
                .type(type)
                .build();

            UUID requestId = idGenerator.randomUuid();
            SkyXploreWsEvent event = SkyXploreWsEvent.builder()
                .eventName(SkyXploreWsEventName.LOAD_GAME_ITEM)
                .id(requestId)
                .payload(request)
                .build();


            wsClient.send(event);

            SkyXploreWsEvent response = wsClient.awaitForEvent(ev -> ev.getEventName() == SkyXploreWsEventName.LOAD_GAME_ITEM && ev.getId().equals(requestId));

            return Optional.ofNullable(response.getPayload())
                .map(s -> (T) objectMapperWrapper.convertValue(s, type.getModelType()));
        } finally {
            wsClientCache.returnObject(wsClient);
        }
    }

    @SneakyThrows
    public <T extends GameItem> List<T> loadChildren(UUID parent, GameItemType type, Class<T[]> clazz) {
        SkyXploreWsClient wsClient = wsClientCache.borrowObject();

        try {
            LoadChildrenOfGameItemRequest request = LoadChildrenOfGameItemRequest.builder()
                .parent(parent)
                .type(type)
                .build();

            UUID requestId = idGenerator.randomUuid();
            SkyXploreWsEvent event = SkyXploreWsEvent.builder()
                .eventName(SkyXploreWsEventName.LOAD_CHILDREN_OF_GAME_ITEM)
                .id(requestId)
                .payload(request)
                .build();

            wsClient.send(event);

            SkyXploreWsEvent response = wsClient.awaitForEvent(ev -> ev.getEventName() == SkyXploreWsEventName.LOAD_CHILDREN_OF_GAME_ITEM && ev.getId().equals(requestId));

            return Arrays.asList(objectMapperWrapper.convertValue(response.getPayload(), clazz));
        } finally {
            wsClientCache.returnObject(wsClient);
        }
    }
}
