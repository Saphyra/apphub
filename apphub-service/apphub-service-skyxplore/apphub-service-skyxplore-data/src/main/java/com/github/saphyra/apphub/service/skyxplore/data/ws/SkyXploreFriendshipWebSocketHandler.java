package com.github.saphyra.apphub.service.skyxplore.data.ws;

import com.github.saphyra.apphub.lib.config.common.endpoints.skyxplore.GenericSkyXploreEndpoints;
import com.github.saphyra.apphub.lib.web_socket.core.handler.AbstractWebSocketHandler;
import com.github.saphyra.apphub.lib.web_socket.core.handler.WebSocketHandlerContext;
import org.springframework.stereotype.Component;

@Component
public class SkyXploreFriendshipWebSocketHandler extends AbstractWebSocketHandler {
    public SkyXploreFriendshipWebSocketHandler(WebSocketHandlerContext context) {
        super(context);
    }

    @Override
    public String getEndpoint() {
        return GenericSkyXploreEndpoints.WS_CONNECTION_SKYXPLORE_MAIN_MENU;
    }
}
