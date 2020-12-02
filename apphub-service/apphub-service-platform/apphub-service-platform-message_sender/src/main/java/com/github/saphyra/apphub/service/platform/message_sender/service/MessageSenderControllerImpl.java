package com.github.saphyra.apphub.service.platform.message_sender.service;

import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import com.github.saphyra.apphub.api.platform.message_sender.server.MessageSenderController;
import com.github.saphyra.apphub.lib.common_util.map.OptionalHashMap;
import com.github.saphyra.apphub.lib.common_util.map.OptionalMap;
import com.github.saphyra.apphub.service.platform.message_sender.connection.ConnectionGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class MessageSenderControllerImpl implements MessageSenderController {
    private final OptionalMap<MessageGroup, ConnectionGroup> connectionGroups;

    public MessageSenderControllerImpl(List<ConnectionGroup> connectionGroups) {
        this.connectionGroups = new OptionalHashMap<>(connectionGroups.stream()
            .collect(Collectors.toMap(ConnectionGroup::getGroup, Function.identity())));
    }

    @Override
    //TODO unit test
    //TODO int test
    public void sendMessage(MessageGroup group, UUID id, String message) {
        ConnectionGroup connectionGroup = connectionGroups.getOptional(group)
            .orElseThrow(() -> new RuntimeException("ConnectionGroup not found for MessageGroup " + group)); //TODO proper exception

        connectionGroup.sendEvent(id, message);
    }
}
