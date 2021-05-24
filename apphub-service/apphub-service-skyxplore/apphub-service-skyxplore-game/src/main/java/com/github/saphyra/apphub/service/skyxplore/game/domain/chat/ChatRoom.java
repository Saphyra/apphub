package com.github.saphyra.apphub.service.skyxplore.game.domain.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class ChatRoom {
    private String id;
    private List<UUID> members;
}
