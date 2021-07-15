package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.system_connection;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SystemConnectionModel;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class SystemConnectionConverter extends ConverterBase<SystemConnectionEntity, SystemConnectionModel> {
    private final UuidConverter uuidConverter;

    @Override
    protected SystemConnectionModel processEntityConversion(SystemConnectionEntity entity) {
        SystemConnectionModel model = new SystemConnectionModel();
        model.setId(uuidConverter.convertEntity(entity.getSystemConnectionId()));
        model.setGameId(uuidConverter.convertEntity(entity.getGameId()));
        model.setType(GameItemType.SYSTEM_CONNECTION);
        return model;
    }

    @Override
    protected SystemConnectionEntity processDomainConversion(SystemConnectionModel domain) {
        return SystemConnectionEntity.builder()
            .systemConnectionId(uuidConverter.convertDomain(domain.getId()))
            .gameId(uuidConverter.convertDomain(domain.getGameId()))
            .build();
    }
}
