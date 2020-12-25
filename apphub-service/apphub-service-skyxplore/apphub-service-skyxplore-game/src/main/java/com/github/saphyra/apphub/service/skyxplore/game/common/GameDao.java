package com.github.saphyra.apphub.service.skyxplore.game.common;

import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
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
//TODO unit test
public class GameDao {
    private final Map<UUID, Game> repository = new ConcurrentHashMap<>();

    public void save(Game game) {
        repository.put(game.getGameId(), game);
        //TODO sync with database
    }

    public int size() {
        return repository.size();
    }

    public void deleteAll() {
        repository.clear();
    }

    public Game findByUserIdValidated(UUID userId) {
        return findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Game not found for user " + userId));
    }

    public Optional<Game> findByUserId(UUID userId) {
        return repository.values()
            .stream()
            .filter(game -> game.getPlayers().containsKey(userId))
            .findFirst();
    }
}
