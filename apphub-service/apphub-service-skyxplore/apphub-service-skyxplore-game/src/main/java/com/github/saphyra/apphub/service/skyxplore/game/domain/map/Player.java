package com.github.saphyra.apphub.service.skyxplore.game.domain.map;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Player {
    private final UUID playerId;
    private final UUID userId;
    private String playerName;
    private UUID allianceId;
    private boolean ai;
    private boolean connected;
}
