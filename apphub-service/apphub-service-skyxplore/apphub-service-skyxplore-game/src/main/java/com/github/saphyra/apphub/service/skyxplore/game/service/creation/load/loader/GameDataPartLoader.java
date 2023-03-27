package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
//TODO unit test
//TODO implement
public abstract class GameDataPartLoader<Model extends GameItem, Type> {
    private static final int FIRST_PAGE = 1;

    private final GameItemLoader gameItemLoader;

    public void load(GameData gameData) {
        int currentPage = FIRST_PAGE;
        while (true) {
            List<Model> items = gameItemLoader.loadPageForGame(gameData.getGameId(), currentPage, getGameItemType(), getClassArray());

            if (items.isEmpty()) {
                return;
            }

            currentPage += 1;

            items.stream()
                .map(this::convert)
                .forEach(item -> addToData(gameData, item));
        }
    }

    protected abstract GameItemType getGameItemType();

    protected abstract Class<Model[]> getClassArray();

    protected abstract Type convert(Model model);

    protected abstract void addToData(GameData gameData, Type item);
}
