package com.github.saphyra.apphub.service.skyxplore.game.proxy;

import com.github.saphyra.apphub.api.platform.message_sender.client.MessageSenderApiClient;
import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.lib.web_utils.CustomLocaleProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageSenderProxy {
    private final CustomLocaleProvider localeProvider;
    private final MessageSenderApiClient messageSenderClient;

    public void sendToLobby(WebSocketMessage message) {
        messageSenderClient.sendMessage(MessageGroup.SKYXPLORE_LOBBY, message, localeProvider.getLocale());
    }

    public void sendToGame(WebSocketMessage message) {
        messageSenderClient.sendMessage(MessageGroup.SKYXPLORE_GAME, message, localeProvider.getLocale());
    }
}
