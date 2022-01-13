package com.github.saphyra.apphub.integration.backend.ws;

import com.github.saphyra.apphub.integration.backend.ws.model.WebSocketEvent;
import com.github.saphyra.apphub.integration.backend.ws.model.WebSocketEventName;
import com.github.saphyra.apphub.integration.common.framework.BiWrapper;
import com.github.saphyra.apphub.integration.common.framework.CollectionUtils;

import java.util.Map;
import java.util.UUID;

public class WsActions {
    public static void sendSkyXplorePageOpenedMessage(ApphubWsClient client, String pageType, UUID pageId) {
        Map<String, Object> payload = CollectionUtils.toMap(
            new BiWrapper<>("pageType", pageType),
            new BiWrapper<>("pageId", pageId)
        );

        WebSocketEvent event = WebSocketEvent.builder()
            .eventName(WebSocketEventName.SKYXPLORE_GAME_PAGE_OPENED)
            .payload(payload)
            .build();
        client.send(event);
    }
}
