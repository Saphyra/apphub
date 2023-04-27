package com.github.saphyra.apphub.api.platform.message_sender.client;

import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;


@FeignClient(name = "message-sender", url = "${serviceUrls.messageSender}")
public interface MessageSenderApiClient {
    @PostMapping(Endpoints.WEB_SOCKET_SEND_MESSAGE)
    void sendMessage(@PathVariable("group") MessageGroup group, @RequestBody WebSocketMessage message, @RequestHeader(Constants.LOCALE_HEADER) String locale);

    @PostMapping(Endpoints.WEB_SOCKET_BULK_SEND_MESSAGE)
    void sendMessage(@PathVariable("group") MessageGroup group, @RequestBody List<WebSocketMessage> messages, @RequestHeader(Constants.LOCALE_HEADER) String locale);
}
