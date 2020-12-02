package com.github.saphyra.apphub.api.platform.message_sender.server;

import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

public interface MessageSenderController {
    @PostMapping(Endpoints.SEND_MESSAGE)
    void sendMessage(@PathVariable("group") MessageGroup group, @PathVariable("id") UUID id, @RequestBody String message);
}
