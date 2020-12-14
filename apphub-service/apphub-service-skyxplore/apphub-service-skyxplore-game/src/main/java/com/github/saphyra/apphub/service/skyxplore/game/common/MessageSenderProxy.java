package com.github.saphyra.apphub.service.skyxplore.game.common;

import com.github.saphyra.apphub.api.platform.message_sender.client.MessageSenderApiClient;
import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class MessageSenderProxy {
    private final CustomLocaleProvider localeProvider;
    private final MessageSenderApiClient messageSenderClient;

    public void sendToLobby(WebSocketMessage message) {
        messageSenderClient.sendMessage(MessageGroup.SKYXPLORE_LOBBY, message, localeProvider.getLocale());
    }
}
