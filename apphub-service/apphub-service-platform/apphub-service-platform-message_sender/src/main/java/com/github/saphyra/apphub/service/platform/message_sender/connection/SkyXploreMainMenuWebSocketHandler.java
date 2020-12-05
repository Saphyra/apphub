package com.github.saphyra.apphub.service.platform.message_sender.connection;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@Slf4j
//TODO unit test
public class SkyXploreMainMenuWebSocketHandler extends DefaultWebSocketHandler {
    public SkyXploreMainMenuWebSocketHandler(WebSocketHandlerContext context) {
        super(context);
    }

    @Override
    public MessageGroup getGroup() {
        return MessageGroup.SKYXPLORE_MAIN_MENU;
    }

    @Override
    protected void handleMessage(UUID userId, WebSocketEvent event) {
        throw new UnsupportedOperationException("Client should not send message from SkyXplore mainMenu");
    }
}
