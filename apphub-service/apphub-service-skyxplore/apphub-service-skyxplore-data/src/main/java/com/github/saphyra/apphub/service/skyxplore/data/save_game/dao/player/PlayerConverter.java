package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.player;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlayerModel;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class PlayerConverter extends ConverterBase<PlayerEntity, PlayerModel> {
    private final UuidConverter uuidConverter;

    @Override
    protected PlayerModel processEntityConversion(PlayerEntity entity) {
        PlayerModel model = new PlayerModel();
        model.setId(uuidConverter.convertEntity(entity.getPlayerId()));
        model.setUserId(uuidConverter.convertEntity(entity.getUserId()));
        model.setGameId(uuidConverter.convertEntity(entity.getGameId()));
        model.setType(GameItemType.PLAYER);
        model.setAllianceId(uuidConverter.convertEntity(entity.getAllianceId()));
        model.setUsername(entity.getUsername());
        model.setAi(entity.isAi());
        return model;
    }

    @Override
    protected PlayerEntity processDomainConversion(PlayerModel domain) {
        return PlayerEntity.builder()
            .playerId(uuidConverter.convertDomain(domain.getId()))
            .gameId(uuidConverter.convertDomain(domain.getGameId()))
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .allianceId(uuidConverter.convertDomain(domain.getAllianceId()))
            .username(domain.getUsername())
            .ai(domain.getAi())
            .build();
    }
}
