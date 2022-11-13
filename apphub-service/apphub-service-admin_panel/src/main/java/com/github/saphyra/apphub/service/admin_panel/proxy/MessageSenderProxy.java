package com.github.saphyra.apphub.service.admin_panel.proxy;

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
    private final MessageSenderApiClient messageSenderClient;
    private final LocaleProvider localeProvider;

    public void sendToMonitoring(WebSocketMessage message) {
        messageSenderClient.sendMessage(MessageGroup.ADMIN_PANEL_MONITORING, message, localeProvider.getOrDefault());
    }

    public void sendToErrorReport(WebSocketMessage message) {
        messageSenderClient.sendMessage(MessageGroup.ADMIN_PANEL_ERROR_REPORT, message, localeProvider.getOrDefault());
    }
}
