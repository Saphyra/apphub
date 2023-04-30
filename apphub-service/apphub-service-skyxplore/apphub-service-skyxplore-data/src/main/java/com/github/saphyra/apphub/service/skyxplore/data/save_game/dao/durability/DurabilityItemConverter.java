package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.durability;

import com.github.saphyra.apphub.api.skyxplore.model.game.DurabilityModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class DurabilityItemConverter extends ConverterBase<DurabilityEntity, DurabilityModel> {
    private final UuidConverter uuidConverter;

    @Override
    protected DurabilityModel processEntityConversion(DurabilityEntity entity) {
        DurabilityModel model = new DurabilityModel();
        model.setId(uuidConverter.convertEntity(entity.getDurabilityId()));
        model.setGameId(uuidConverter.convertEntity(entity.getGameId()));
        model.setType(GameItemType.DURABILITY);
        model.setExternalReference(uuidConverter.convertEntity(entity.getExternalReference()));
        model.setMaxHitPoints(entity.getMaxHitPoints());
        model.setCurrentHitPoints(entity.getCurrentHitPoints());
        return model;
    }

    @Override
    protected DurabilityEntity processDomainConversion(DurabilityModel domain) {
        return DurabilityEntity.builder()
            .durabilityId(uuidConverter.convertDomain(domain.getId()))
            .gameId(uuidConverter.convertDomain(domain.getGameId()))
            .externalReference(uuidConverter.convertDomain(domain.getExternalReference()))
            .maxHitPoints(domain.getMaxHitPoints())
            .currentHitPoints(domain.getCurrentHitPoints())
            .build();
    }
}
