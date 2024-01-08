package com.github.saphyra.apphub.integration.ws;

import com.github.saphyra.apphub.integration.core.ForRemoval;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.CollectionUtils;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEvent;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEventName;

import java.util.Map;
import java.util.UUID;

public class WsActions {
    @ForRemoval("skyxplore-react")
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
