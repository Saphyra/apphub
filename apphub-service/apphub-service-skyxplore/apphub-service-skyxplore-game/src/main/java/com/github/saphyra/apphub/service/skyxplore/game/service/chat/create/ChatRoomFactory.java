package com.github.saphyra.apphub.service.skyxplore.game.service.chat.create;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.ChatRoom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class ChatRoomFactory {
    private final IdGenerator idGenerator;

    ChatRoom create(UUID creator, String roomTitle, List<UUID> m) {
        List<UUID> members = new ArrayList<>();
        members.add(creator);
        members.addAll(m);

        return ChatRoom.builder()
            .id(idGenerator.generateRandomId())
            .roomTitle(roomTitle)
            .members(members)
            .build();
    }
}
