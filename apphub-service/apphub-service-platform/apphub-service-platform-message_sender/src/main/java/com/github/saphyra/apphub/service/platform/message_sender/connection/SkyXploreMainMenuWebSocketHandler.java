package com.github.saphyra.apphub.service.platform.message_sender.connection;

import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

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
}
