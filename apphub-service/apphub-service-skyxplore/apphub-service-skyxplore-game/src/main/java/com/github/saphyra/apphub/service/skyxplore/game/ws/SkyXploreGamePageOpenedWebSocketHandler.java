package com.github.saphyra.apphub.service.skyxplore.game.ws;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
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
    public void handle(UUID from, WebSocketEvent event) {
        OpenedPage openedPage = objectMapperWrapper.convertValue(event.getPayload(), OpenedPage.class);

        ValidationUtil.notNull(openedPage.getOpenedPageType(), "pageType");
        log.info("{} opened page {}", from, openedPage);

        gameDao.findByUserIdValidated(from)
            .getPlayers()
            .get(from)
            .setOpenedPage(openedPage);
    }
}
