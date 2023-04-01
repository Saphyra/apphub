package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;

import java.util.List;

public abstract class AutoLoader<Model extends GameItem, Type> extends AbstractGameItemLoader<Model> {
    public AutoLoader(GameItemLoader gameItemLoader) {
        super(gameItemLoader);
    }

    public void autoLoad(GameData gameData) {
        List<Type> items = getByGameId(gameData.getGameId())
            .stream()
            .map(this::convert)
            .toList();

        addToGameData(gameData, items);
    }

    protected abstract void addToGameData(GameData gameData, List<Type> items);

    protected abstract Type convert(Model model);
}
