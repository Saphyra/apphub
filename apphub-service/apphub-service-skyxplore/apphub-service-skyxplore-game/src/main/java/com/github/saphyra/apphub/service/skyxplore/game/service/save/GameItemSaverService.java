package com.github.saphyra.apphub.service.skyxplore.game.service.save;

import com.github.saphyra.apphub.api.skyxplore.data.client.SkyXploreDataGameClient;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.lib.common_util.ExecutorServiceBean;
import com.github.saphyra.apphub.service.skyxplore.game.common.CustomLocaleProvider;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class GameItemSaverService {
    private final ExecutorServiceBean executorServiceBean;
    private final SaverProperties saverProperties;
    private final SkyXploreDataGameClient gameClient;
    private final CustomLocaleProvider customLocaleProvider;

    public void saveAsync(List<GameItem> items) {
        Lists.partition(items, saverProperties.getMaxChunkSize())
            .forEach(this::doSaveAsync);
    }

    private void doSaveAsync(List<GameItem> items) {
        executorServiceBean.execute(() -> gameClient.saveGameData(items, customLocaleProvider.getLocale()));
    }
}
