package com.github.saphyra.apphub.service.skyxplore.game.common;

import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
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
}
