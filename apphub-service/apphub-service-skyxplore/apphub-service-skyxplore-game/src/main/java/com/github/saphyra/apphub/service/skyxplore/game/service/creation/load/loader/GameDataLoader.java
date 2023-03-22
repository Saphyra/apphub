package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class GameDataLoader {
    private final List<GameDataPartLoader<?, ?>> loaders;
    private final SleepService sleepService;
    private final ExecutorServiceBean executorServiceBean;

    GameData load(UUID gameId, Integer universeSize) {
        GameData gameData = GameData.builder()
            .gameId(gameId)
            .universeSize(universeSize)
            .build();

        List<Future<ExecutionResult<Void>>> futures = loaders.stream()
            .map(loader -> executorServiceBean.execute(() -> loader.load(gameData)))
            .toList();

        while (futures.stream().anyMatch(future -> !future.isDone())) {
            sleepService.sleep(100);
        }

        return gameData;
    }
}
