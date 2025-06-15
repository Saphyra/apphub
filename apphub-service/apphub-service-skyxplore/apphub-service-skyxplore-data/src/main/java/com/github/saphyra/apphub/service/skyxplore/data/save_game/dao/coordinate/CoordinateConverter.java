package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.coordinate;

import com.github.saphyra.apphub.api.skyxplore.model.game.CoordinateModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class CoordinateConverter extends ConverterBase<CoordinateEntity, CoordinateModel> {
    private final UuidConverter uuidConverter;

    @Override
    protected CoordinateModel processEntityConversion(CoordinateEntity entity) {
        CoordinateModel result = new CoordinateModel();
        result.setId(uuidConverter.convertEntity(entity.getCoordinateId()));
        result.setGameId(uuidConverter.convertEntity(entity.getGameId()));
        result.setType(GameItemType.COORDINATE);
        result.setReferenceId(uuidConverter.convertEntity((entity.getReferenceId())));
        result.setCoordinate(new Coordinate(entity.getX(), entity.getY()));
        result.setOrder(entity.getOrder());
        return result;
    }

    @Override
    protected CoordinateEntity processDomainConversion(CoordinateModel domain) {
        return CoordinateEntity.builder()
            .coordinateId(uuidConverter.convertDomain(domain.getId()))
            .gameId(uuidConverter.convertDomain(domain.getGameId()))
            .referenceId(uuidConverter.convertDomain(domain.getReferenceId()))
            .x(domain.getCoordinate().getX())
            .y(domain.getCoordinate().getY())
            .order(domain.getOrder())
            .build();
    }
}
