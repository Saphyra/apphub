package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.service.skyxplore.game.domain.OpenedPageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class WsMessageKeyFactory {
    WsMessageKey create(UUID recipient, WebSocketEventName eventName,  UUID objectId, OpenedPageType pageType, UUID pageId) {
        return create(recipient, eventName, objectId, List.of(pageType), pageId);
    }

    WsMessageKey create(UUID recipient, WebSocketEventName eventName,  UUID objectId, List<OpenedPageType> pages, UUID pageId) {
        return WsMessageKey.builder()
            .recipient(recipient)
            .eventName(eventName)
            .objectId(objectId)
            .pages(pages)
            .pageId(pageId)
            .build();
    }
}
