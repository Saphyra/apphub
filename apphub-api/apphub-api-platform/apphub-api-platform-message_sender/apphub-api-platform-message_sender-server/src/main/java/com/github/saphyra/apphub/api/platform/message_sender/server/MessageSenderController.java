package com.github.saphyra.apphub.api.platform.message_sender.server;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

public interface MessageSenderController {
    @PostMapping(Endpoints.WEB_SOCKET_SEND_MESSAGE)
    List<UUID> sendMessage(@PathVariable("group") MessageGroup group, @RequestBody WebSocketMessage message);
}
