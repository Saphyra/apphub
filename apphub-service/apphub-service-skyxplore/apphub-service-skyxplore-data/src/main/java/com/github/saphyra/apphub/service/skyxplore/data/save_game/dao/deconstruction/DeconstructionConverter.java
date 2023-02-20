package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.deconstruction;

import com.github.saphyra.apphub.api.skyxplore.model.game.DeconstructionModel;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class DeconstructionConverter extends ConverterBase<DeconstructionEntity, DeconstructionModel> {
    private final UuidConverter uuidConverter;

    @Override
    protected DeconstructionEntity processDomainConversion(DeconstructionModel domain) {
        return DeconstructionEntity.builder()
            .deconstructionId(uuidConverter.convertDomain(domain.getId()))
            .gameId(uuidConverter.convertDomain(domain.getGameId()))
            .externalReference(uuidConverter.convertDomain(domain.getExternalReference()))
            .currentWorkPoints(domain.getCurrentWorkPoints())
            .priority(domain.getPriority())
            .build();
    }

    @Override
    protected DeconstructionModel processEntityConversion(DeconstructionEntity entity) {
        DeconstructionModel model = new DeconstructionModel();
        model.setId(uuidConverter.convertEntity(entity.getDeconstructionId()));
        model.setGameId(uuidConverter.convertEntity(entity.getGameId()));
        model.setExternalReference(uuidConverter.convertEntity(entity.getExternalReference()));
        model.setCurrentWorkPoints(entity.getCurrentWorkPoints());
        model.setPriority(entity.getPriority());
        return model;
    }
}
