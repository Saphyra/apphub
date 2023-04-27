package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractGameItemLoader<Model extends GameItem> {
    private static final int FIRST_PAGE = 0;

    private final GameItemLoader gameItemLoader;

    public List<Model> getByGameId(UUID gameId) {
        List<Model> result = new ArrayList<>();

        int currentPage = FIRST_PAGE;
        while (true) {
            log.info("Loading page #{} of {} for gameId {}", currentPage, getGameItemType(), gameId);
            List<Model> items = gameItemLoader.loadPageForGame(gameId, currentPage, getGameItemType(), getArrayClass());

            if (items.isEmpty()) {
                return result;
            }

            result.addAll(items);

            currentPage += 1;
        }
    }

    protected abstract GameItemType getGameItemType();

    protected abstract Class<Model[]> getArrayClass();
}
