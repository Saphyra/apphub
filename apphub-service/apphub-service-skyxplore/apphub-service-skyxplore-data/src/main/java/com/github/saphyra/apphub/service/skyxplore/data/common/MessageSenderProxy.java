package com.github.saphyra.apphub.service.skyxplore.data.common;

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
    private final MessageSenderApiClient messageSenderClient;
    private final CustomLocaleProvider customLocaleProvider;

    public void sendToMainMenu(WebSocketMessage message) {
        messageSenderClient.sendMessage(MessageGroup.SKYXPLORE_MAIN_MENU, message, customLocaleProvider.getLocale());
    }
}
