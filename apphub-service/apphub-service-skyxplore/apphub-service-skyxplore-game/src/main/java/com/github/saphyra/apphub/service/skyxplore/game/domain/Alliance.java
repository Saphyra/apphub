package com.github.saphyra.apphub.service.skyxplore.game.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Alliance {
    private final UUID allianceId;
    private final String allianceName;
    private final Map<UUID, Player> members;
}
