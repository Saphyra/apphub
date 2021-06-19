package com.github.saphyra.apphub.service.skyxplore.game.service.chat.create;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class ChatRoomCreatedMessage {
    private String id;
    private String title;
}
