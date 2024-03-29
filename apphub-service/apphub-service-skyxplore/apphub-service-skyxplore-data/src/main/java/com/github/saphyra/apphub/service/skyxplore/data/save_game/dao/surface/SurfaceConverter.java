package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.surface;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SurfaceModel;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class SurfaceConverter extends ConverterBase<SurfaceEntity, SurfaceModel> {
    private final UuidConverter uuidConverter;

    @Override
    protected SurfaceModel processEntityConversion(SurfaceEntity entity) {
        SurfaceModel model = new SurfaceModel();
        model.setId(uuidConverter.convertEntity(entity.getSurfaceId()));
        model.setGameId(uuidConverter.convertEntity(entity.getGameId()));
        model.setType(GameItemType.SURFACE);
        model.setPlanetId(uuidConverter.convertEntity(entity.getPlanetId()));
        model.setSurfaceType(entity.getSurfaceType());
        return model;
    }

    @Override
    protected SurfaceEntity processDomainConversion(SurfaceModel domain) {
        return SurfaceEntity.builder()
            .surfaceId(uuidConverter.convertDomain(domain.getId()))
            .gameId(uuidConverter.convertDomain(domain.getGameId()))
            .planetId(uuidConverter.convertDomain(domain.getPlanetId()))
            .surfaceType(domain.getSurfaceType())
            .build();
    }
}
