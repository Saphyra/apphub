package com.github.saphyra.apphub.service.skyxplore.game.common;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.google.common.annotations.VisibleForTesting;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class GameDao {
    private final ExecutorServiceBean executorServiceBean;
    private final SleepService sleepService;

    @Getter(value = AccessLevel.PACKAGE)
    private final Map<UUID, Game> repository = new ConcurrentHashMap<>();

    public void save(Game game) {
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
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.GAME_NOT_FOUND, "Game not found for user " + userId));
    }

    public Optional<Game> findByUserId(UUID userId) {
        return repository.values()
            .stream()
            .filter(game -> game.getPlayers().containsKey(userId))
            .filter(game -> !game.isTerminated())
            .findFirst();
    }

    @VisibleForTesting
    public void put(Game game) {
        repository.put(Optional.ofNullable(game.getGameId()).orElse(UUID.randomUUID()), game);
    }

    public void delete(UUID gameId) {
        Game game = repository.get(gameId);

        if (!isNull(game)) {
            delete(game);
        }
    }

    public void delete(Game game) {
        log.info("Deleting game {} from cache", game.getGameId());

        game.setTerminated(true);
        executorServiceBean.execute(() -> {
            for (int i = 0; i < 60; i++) {
                int queueSize = game.getEventLoop().getQueueSize();
                if (queueSize == 0) {
                    sleepService.sleep(10000);
                    stopGame(game);
                    return;
                }

                log.debug("Queue still has {} items left.", queueSize);

                sleepService.sleep(1000);
            }

            int queueSize = game.getEventLoop().getQueueSize();
            stopGame(game);

            throw new IllegalStateException("Queue still has " + queueSize + " number of items");
        });
    }

    private void stopGame(Game game) {
        game.getEventLoop()
            .stop();
        log.info("EventLoop is shut down.");
        repository.remove(game.getGameId());
        log.info("GameDao still has {} number of games left.", repository.size());
    }

    public List<Game> getAll() {
        return new ArrayList<>(repository.values());
    }
}
