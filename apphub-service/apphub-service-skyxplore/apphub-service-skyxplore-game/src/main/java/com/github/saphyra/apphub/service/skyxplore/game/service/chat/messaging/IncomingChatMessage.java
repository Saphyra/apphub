package com.github.saphyra.apphub.service.skyxplore.game.service.chat.messaging;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
class IncomingChatMessage {
    private String room;
    private String message;
}
