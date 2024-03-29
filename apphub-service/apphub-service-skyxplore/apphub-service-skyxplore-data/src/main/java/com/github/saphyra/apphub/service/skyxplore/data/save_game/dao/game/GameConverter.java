package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.game;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class GameConverter extends ConverterBase<GameEntity, GameModel> {
    private final UuidConverter uuidConverter;

    @Override
    protected GameModel processEntityConversion(GameEntity entity) {
        GameModel model = new GameModel();
        model.setId(uuidConverter.convertEntity(entity.getGameId()));
        model.setGameId(uuidConverter.convertEntity(entity.getGameId()));
        model.setType(GameItemType.GAME);
        model.setName(entity.getName());
        model.setHost(uuidConverter.convertEntity(entity.getHost()));
        model.setLastPlayed(entity.getLastPlayed());
        model.setMarkedForDeletion(entity.getMarkedForDeletion());
        model.setMarkedForDeletionAt(entity.getMarkedForDeletionAt());
        model.setUniverseSize(entity.getUniverseSize());
        return model;
    }

    @Override
    protected GameEntity processDomainConversion(GameModel domain) {
        return GameEntity.builder()
            .gameId(uuidConverter.convertDomain(domain.getGameId()))
            .name(domain.getName())
            .host(uuidConverter.convertDomain(domain.getHost()))
            .lastPlayed(domain.getLastPlayed())
            .markedForDeletion(domain.getMarkedForDeletion())
            .markedForDeletionAt(domain.getMarkedForDeletionAt())
            .universeSize(domain.getUniverseSize())
            .build();
    }
}
