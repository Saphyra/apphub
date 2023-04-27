package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;

import java.util.List;
import java.util.UUID;

public interface GameItemService {
    GameItemType getType();

    void deleteByGameId(UUID gameId);

    void save(List<GameItem> gameItems);

    void deleteById(UUID id);

    List<? extends GameItem> loadPage(UUID gameId, Integer page, Integer itemsPerPage);
}
