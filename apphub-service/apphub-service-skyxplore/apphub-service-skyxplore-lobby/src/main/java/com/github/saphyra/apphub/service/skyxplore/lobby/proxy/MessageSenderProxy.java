package com.github.saphyra.apphub.service.skyxplore.lobby.proxy;

import com.github.saphyra.apphub.api.platform.message_sender.client.MessageSenderApiClient;
import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageSenderProxy {
    private final MessageSenderApiClient messageSenderApiClient;
    private final LocaleProvider localeProvider;

    public void sendToMainMenu(WebSocketMessage message) {
        messageSenderApiClient.sendMessage(MessageGroup.SKYXPLORE_MAIN_MENU, message, localeProvider.getLocaleValidated());
    }

    public void sendToLobby(WebSocketMessage message) {
        messageSenderApiClient.sendMessage(MessageGroup.SKYXPLORE_LOBBY, message, localeProvider.getLocaleValidated());
    }
}
