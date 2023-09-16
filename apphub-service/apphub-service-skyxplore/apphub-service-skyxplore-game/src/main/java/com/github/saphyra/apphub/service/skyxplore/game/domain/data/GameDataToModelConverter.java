package com.github.saphyra.apphub.service.skyxplore.game.domain.data;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;

import java.util.List;
import java.util.UUID;

public interface GameDataToModelConverter {
    List< ? extends GameItem> convert(UUID gameId, GameData gameData);
}
