package com.github.saphyra.apphub.service.skyxplore.game.domain.chat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Chat {
    private final List<UUID> players;
    private final List<ChatRoom> rooms;
}
