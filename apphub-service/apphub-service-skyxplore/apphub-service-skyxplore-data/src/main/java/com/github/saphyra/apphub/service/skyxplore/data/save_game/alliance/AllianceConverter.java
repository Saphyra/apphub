package com.github.saphyra.apphub.service.skyxplore.data.save_game.alliance;

import com.github.saphyra.apphub.api.skyxplore.model.game.AllianceModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class AllianceConverter extends ConverterBase<AllianceEntity, AllianceModel> {
    private final UuidConverter uuidConverter;

    @Override
    protected AllianceModel processEntityConversion(AllianceEntity entity) {
        AllianceModel model = new AllianceModel();
        model.setId(uuidConverter.convertEntity(entity.getAllianceId()));
        model.setGameId(uuidConverter.convertEntity(entity.getGameId()));
        model.setType(GameItemType.ALLIANCE);
        model.setName(entity.getName());
        return model;
    }

    @Override
    protected AllianceEntity processDomainConversion(AllianceModel domain) {
        return AllianceEntity.builder()
            .allianceId(uuidConverter.convertDomain(domain.getId()))
            .gameId(uuidConverter.convertDomain(domain.getGameId()))
            .name(domain.getName())
            .build();
    }
}
