package com.github.saphyra.apphub.service.platform.message_sender.connection;

import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@Slf4j
//TODO unit test
public class SkyXploreGameConnectionHandler extends DefaultWebSocketHandler {
    public SkyXploreGameConnectionHandler(WebSocketHandlerContext context) {
        super(context);
    }

    @Override
    public MessageGroup getGroup() {
        return MessageGroup.SKYXPLORE_GAME;
    }

    @Override
    protected void afterConnection(UUID userId) {
        //TODO
    }

    @Override
    protected void afterDisconnection(UUID userId) {
        //TODO
    }
}
