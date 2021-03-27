package com.github.saphyra.apphub.service.skyxplore.data.save_game;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;

import java.util.List;
import java.util.UUID;

public interface GameItemService {
    void deleteByGameId(UUID gameId);

    GameItemType getType();

    void save(List<GameItem> gameItems);
}
