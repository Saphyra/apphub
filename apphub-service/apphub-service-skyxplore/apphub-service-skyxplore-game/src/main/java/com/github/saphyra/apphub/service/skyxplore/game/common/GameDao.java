package com.github.saphyra.apphub.service.skyxplore.game.common;

import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.NotFoundException;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.GameSaverService;
import com.google.common.annotations.VisibleForTesting;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class GameDao {
    @Getter(value = AccessLevel.PACKAGE)
    private final Map<UUID, Game> repository = new ConcurrentHashMap<>();

    private final GameSaverService gameSaverService;

    public void save(Game game) {
        gameSaverService.save(game);
        repository.put(game.getGameId(), game);
    }

    @VisibleForTesting
    public int size() {
        return repository.size();
    }

    @VisibleForTesting
    public void deleteAll() {
        repository.clear();
    }

    public Game findByUserIdValidated(UUID userId) {
        return findByUserId(userId)
            .orElseThrow(() -> new NotFoundException(new ErrorMessage(ErrorCode.GAME_NOT_FOUND.name()), "Game not found for user " + userId));
    }

    public Optional<Game> findByUserId(UUID userId) {
        return repository.values()
            .stream()
            .filter(game -> game.getPlayers().containsKey(userId))
            .findFirst();
    }
}
