package com.github.saphyra.apphub.service.skyxplore.data.save_game.allocated_resource;

import com.github.saphyra.apphub.api.skyxplore.model.game.AllocatedResourceModel;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class AllocatedResourceConverter extends ConverterBase<AllocatedResourceEntity, AllocatedResourceModel> {
    private final UuidConverter uuidConverter;

    @Override
    protected AllocatedResourceModel processEntityConversion(AllocatedResourceEntity entity) {
        AllocatedResourceModel model = new AllocatedResourceModel();
        model.setId(uuidConverter.convertEntity(entity.getAllocatedResourceId()));
        model.setGameId(uuidConverter.convertEntity(entity.getGameId()));
        model.setLocation(uuidConverter.convertEntity(entity.getLocation()));
        model.setLocationType(entity.getLocationType());
        model.setExternalReference(uuidConverter.convertEntity(entity.getExternalReference()));
        model.setDataId(entity.getDataId());
        model.setAmount(entity.getAmount());
        return model;
    }

    @Override
    protected AllocatedResourceEntity processDomainConversion(AllocatedResourceModel domain) {
        return AllocatedResourceEntity.builder()
            .allocatedResourceId(uuidConverter.convertDomain(domain.getId()))
            .gameId(uuidConverter.convertDomain(domain.getGameId()))
            .location(uuidConverter.convertDomain(domain.getLocation()))
            .locationType(domain.getLocationType())
            .externalReference(uuidConverter.convertDomain(domain.getExternalReference()))
            .dataId(domain.getDataId())
            .amount(domain.getAmount())
            .build();
    }
}
