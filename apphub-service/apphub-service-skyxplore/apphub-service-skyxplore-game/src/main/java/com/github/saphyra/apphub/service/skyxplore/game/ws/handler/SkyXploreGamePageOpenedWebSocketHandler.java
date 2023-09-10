package com.github.saphyra.apphub.service.skyxplore.game.ws.handler;

import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.OpenedPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class SkyXploreGamePageOpenedWebSocketHandler implements WebSocketEventHandler {
    private final GameDao gameDao;
    private final ObjectMapperWrapper objectMapperWrapper;

    @Override
    public boolean canHandle(WebSocketEventName eventName) {
        return eventName == WebSocketEventName.SKYXPLORE_GAME_PAGE_OPENED;
    }

    @Override
    public void handle(UUID from, WebSocketEvent event, SkyXploreGameWebSocketHandler webSocketHandler) {
        OpenedPage openedPage = objectMapperWrapper.convertValue(event.getPayload(), OpenedPage.class);

        ValidationUtil.notNull(openedPage.getPageType(), "pageType");
        log.info("{} opened page {}", from, openedPage);

        gameDao.findByUserIdValidated(from)
            .getPlayers()
            .get(from)
            .setOpenedPage(openedPage);
    }
}
