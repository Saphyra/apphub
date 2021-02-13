package com.github.saphyra.apphub.service.skyxplore.game.service.save;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.lib.common_util.ExecutorServiceBean;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.GameToGameItemListConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class GameSaverService {
    private final GameToGameItemListConverter converter;
    private final GameItemSaverService saverService;
    private final ExecutorServiceBean executorServiceBean;

    public void save(Game game) {
        try {
            List<GameItem> items = converter.convertDeep(game);
            executorServiceBean.execute(() -> saverService.saveAsync(items));
        } catch (Exception e) {
            log.error("Exception", e);
            throw e;
        }
    }
}
