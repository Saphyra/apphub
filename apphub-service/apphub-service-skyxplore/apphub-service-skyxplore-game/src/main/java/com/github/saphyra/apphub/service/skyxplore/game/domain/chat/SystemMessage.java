package com.github.saphyra.apphub.service.skyxplore.game.domain.chat;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class SystemMessage {
    private String room;
    private String characterName;
    private UUID userId;
}
