package com.github.saphyra.apphub.service.skyxplore.game.domain;

import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.Chat;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Alliance;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Player;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Slf4j
public class Game {
    private final UUID gameId;
    private final String gameName;
    private final UUID host;
    private final Map<UUID, Player> players; //<UserId, Player>
    private final Map<UUID, Alliance> alliances;
    private final Universe universe;
    private final LocalDateTime lastPlayed;
    private LocalDateTime expiresAt;

    private final Chat chat;

    @Builder.Default
    private volatile boolean gamePaused = true;

    private ScheduledFuture<?> tickScheduler;

    public List<UUID> getConnectedPlayers() {
        return players.values()
            .stream()
            .filter(player -> !player.isAi())
            .filter(Player::isConnected)
            .map(Player::getUserId)
            .collect(Collectors.toList());
    }

    public List<UUID> filterConnectedPlayersFrom(Collection<UUID> members) {
        return members.stream()
            .filter(userId -> !players.get(userId).isAi())
            .filter(userId -> players.get(userId).isConnected())
            .collect(Collectors.toList());
    }

    //TODO unit test
    public boolean shouldRun() {
        if (gamePaused) {
            log.debug("Game {} is paused.", gameId);
            return false;
        }

        log.debug("Game {} should run", gameId);
        return true;
    }
}
