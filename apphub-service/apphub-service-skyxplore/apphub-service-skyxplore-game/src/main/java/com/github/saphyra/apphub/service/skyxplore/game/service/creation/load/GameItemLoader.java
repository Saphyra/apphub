package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.skyxplore.ws.LoadPageForGameRequest;
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
import java.util.UUID;

@RequiredArgsConstructor
@Component
@Slf4j
public class GameItemLoader {
    private final ObjectMapperWrapper objectMapperWrapper;
    private final WebSocketClientCache wsClientCache;
    private final IdGenerator idGenerator;

    @SneakyThrows
    public <T extends GameItem> List<T> loadPageForGame(UUID gameId, int page, GameItemType gameItemType, Class<T[]> clazz) {
        SkyXploreWsClient wsClient = wsClientCache.borrowObject();

        try {
            LoadPageForGameRequest request = LoadPageForGameRequest.builder()
                .gameId(gameId)
                .page(page)
                .type(gameItemType)
                .build();

            UUID requestId = idGenerator.randomUuid();
            SkyXploreWsEvent event = SkyXploreWsEvent.builder()
                .eventName(SkyXploreWsEventName.LOAD_PAGE_FOR_GAME)
                .id(requestId)
                .payload(request)
                .build();

            wsClient.send(event);

            SkyXploreWsEvent response = wsClient.awaitForEvent(ev -> ev.getEventName() == SkyXploreWsEventName.LOAD_PAGE_FOR_GAME && ev.getId().equals(requestId));

            return Arrays.asList(objectMapperWrapper.convertValue(response.getPayload(), clazz));
        } finally {
            wsClientCache.returnObject(wsClient);
        }
    }
}
