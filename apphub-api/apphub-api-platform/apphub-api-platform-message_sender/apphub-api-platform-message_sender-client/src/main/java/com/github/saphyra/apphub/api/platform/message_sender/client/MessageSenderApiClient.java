package com.github.saphyra.apphub.api.platform.message_sender.client;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;


@FeignClient("message-sender")
public interface MessageSenderApiClient {
    /**
     * @return Id of users the message could not be sent to
     */
    @PostMapping(Endpoints.SEND_MESSAGE)
    List<UUID> sendMessage(@PathVariable("group") MessageGroup group, @RequestBody WebSocketMessage message, @RequestHeader(Constants.LOCALE_HEADER) String locale);
}