package com.github.saphyra.apphub.service.skyxplore.game.domain;

import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.Chat;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Alliance;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Player;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import com.github.saphyra.apphub.service.skyxplore.game.domain.process.EventLoop;
import com.github.saphyra.apphub.service.skyxplore.game.domain.process.GameProcesses;
import com.github.saphyra.apphub.service.skyxplore.game.domain.process.ProcessContext;
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
    private final EventLoop eventLoop;
    private final ProcessContext processContext;

    private GameProcesses gameProcesses;

    @Builder.Default
    private volatile boolean gamePaused = true;
    @Builder.Default
    private volatile boolean terminated = false;

    public Game gameProcess() {
        gameProcesses = new GameProcesses(this, processContext);
        return this;
    }

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

    public boolean shouldRun() {
        return !gamePaused;
    }
}
