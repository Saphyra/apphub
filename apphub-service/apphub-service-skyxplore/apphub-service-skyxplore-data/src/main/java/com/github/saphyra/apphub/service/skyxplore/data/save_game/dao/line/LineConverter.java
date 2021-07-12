package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.line;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.LineModel;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class LineConverter extends ConverterBase<LineEntity, LineModel> {
    private final UuidConverter uuidConverter;

    @Override
    protected LineModel processEntityConversion(LineEntity entity) {
        LineModel result = new LineModel();
        result.setId(uuidConverter.convertEntity(entity.getLineId()));
        result.setGameId(uuidConverter.convertEntity(entity.getGameId()));
        result.setType(GameItemType.LINE);
        result.setReferenceId(uuidConverter.convertEntity(entity.getReferenceId()));
        result.setA(uuidConverter.convertEntity(entity.getA()));
        result.setB(uuidConverter.convertEntity(entity.getB()));
        return result;
    }

    @Override
    protected LineEntity processDomainConversion(LineModel domain) {
        return LineEntity.builder()
            .lineId(uuidConverter.convertDomain(domain.getId()))
            .gameId(uuidConverter.convertDomain(domain.getGameId()))
            .referenceId(uuidConverter.convertDomain(domain.getReferenceId()))
            .a(uuidConverter.convertDomain(domain.getA()))
            .b(uuidConverter.convertDomain(domain.getB()))
            .build();
    }
}
