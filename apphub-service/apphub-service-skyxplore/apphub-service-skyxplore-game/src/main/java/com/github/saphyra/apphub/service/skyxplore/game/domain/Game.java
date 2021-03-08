package com.github.saphyra.apphub.service.skyxplore.game.domain;

import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.Chat;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Alliance;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Player;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
//TODO unit test
public class Game {
    private final UUID gameId;
    private final String gameName;
    private final UUID host;
    private final Map<UUID, Player> players; //<UserId, Player>
    private final Map<UUID, Alliance> alliances;
    private final Universe universe;

    private final Chat chat;

    public List<UUID> filterConnectedPlayersFrom(List<UUID> members) {
        return members.stream()
            .filter(userId -> players.get(userId).isConnected())
            .collect(Collectors.toList());
    }
}
