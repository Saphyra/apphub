package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GameItemService {
    void deleteByGameId(UUID gameId);

    GameItemType getType();

    void save(List<GameItem> gameItems);

    Optional<? extends GameItem> findById(UUID id);

    List<? extends GameItem> getByParent(UUID parent);

    void deleteById(UUID id);
}
