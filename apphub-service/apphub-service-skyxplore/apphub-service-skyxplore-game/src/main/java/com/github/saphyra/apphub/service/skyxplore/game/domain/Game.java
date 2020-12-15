package com.github.saphyra.apphub.service.skyxplore.game.domain;

import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Alliance;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Player;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Game {
    private final UUID gameId;
    private final UUID host;
    private final Map<UUID, Player> players;
    private final Map<UUID, Alliance> alliances;
    private final Universe universe;
}
