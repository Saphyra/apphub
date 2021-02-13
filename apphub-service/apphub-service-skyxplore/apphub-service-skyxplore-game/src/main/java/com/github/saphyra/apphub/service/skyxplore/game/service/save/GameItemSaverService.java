package com.github.saphyra.apphub.service.skyxplore.game.service.save;

import com.github.saphyra.apphub.api.skyxplore.data.client.SkyXploreDataGameClient;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.lib.common_util.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.service.skyxplore.game.common.CustomLocaleProvider;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executors;

@Component
@Slf4j
//TODO unit test
public class GameItemSaverService {
    private final ExecutorServiceBean executorServiceBean;
    private final SaverProperties saverProperties;
    private final SkyXploreDataGameClient gameClient;
    private final CustomLocaleProvider customLocaleProvider;

    public GameItemSaverService(
        SaverProperties saverProperties,
        SkyXploreDataGameClient gameClient,
        CustomLocaleProvider customLocaleProvider,
        SleepService sleepService
    ) {
        this.executorServiceBean = new ExecutorServiceBean(Executors.newFixedThreadPool(4), sleepService);
        this.saverProperties = saverProperties;
        this.gameClient = gameClient;
        this.customLocaleProvider = customLocaleProvider;
    }

    public void saveAsync(List<GameItem> items) {
        Lists.partition(items, saverProperties.getMaxChunkSize())
            .forEach(this::doSaveAsync);
    }

    private void doSaveAsync(List<GameItem> items) {
        executorServiceBean.execute(() -> gameClient.saveGameData(items, customLocaleProvider.getLocale()));
    }
}
