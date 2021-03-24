package com.github.saphyra.apphub.service.skyxplore.game.service.chat.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class OutgoingChatMessage {
    private String room;
    private String message;
    private UUID senderId;
    private String senderName;
}
