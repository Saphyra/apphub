package com.github.saphyra.apphub.service.skyxplore.lobby.proxy;

import com.github.saphyra.apphub.api.platform.message_sender.client.MessageSenderApiClient;
import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.lib.common_util.LocaleProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class MessageSenderProxy {
    private final MessageSenderApiClient messageSenderApiClient;
    private final LocaleProvider localeProvider;

    public List<UUID> sendToMainMenu(WebSocketMessage message) {
        return messageSenderApiClient.sendMessage(MessageGroup.SKYXPLORE_MAIN_MENU, message, localeProvider.getLocaleValidated());
    }

    public List<UUID> sendToLobby(WebSocketMessage message) {
        return messageSenderApiClient.sendMessage(MessageGroup.SKYXPLORE_LOBBY, message, localeProvider.getLocaleValidated());
    }
}
