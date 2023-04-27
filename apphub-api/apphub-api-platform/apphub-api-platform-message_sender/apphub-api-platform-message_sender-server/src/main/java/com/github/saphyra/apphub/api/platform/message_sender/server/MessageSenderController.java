package com.github.saphyra.apphub.api.platform.message_sender.server;

import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface MessageSenderController {
    @PostMapping(Endpoints.WEB_SOCKET_SEND_MESSAGE)
    void sendMessage(@PathVariable("group") MessageGroup group, @RequestBody WebSocketMessage message);

    @PostMapping(Endpoints.WEB_SOCKET_BULK_SEND_MESSAGE)
    void sendMessage(@PathVariable("group") MessageGroup group, @RequestBody List<WebSocketMessage> messages);
}
