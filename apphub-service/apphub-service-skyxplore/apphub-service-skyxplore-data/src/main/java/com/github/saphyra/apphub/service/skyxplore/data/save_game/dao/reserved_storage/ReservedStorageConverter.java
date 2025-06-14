package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.reserved_storage;

import com.github.saphyra.apphub.api.skyxplore.model.game.ContainerType;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ReservedStorageModel;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class ReservedStorageConverter extends ConverterBase<ReservedStorageEntity, ReservedStorageModel> {
    private final UuidConverter uuidConverter;

    @Override
    protected ReservedStorageModel processEntityConversion(ReservedStorageEntity entity) {
        ReservedStorageModel model = new ReservedStorageModel();
        model.setId(uuidConverter.convertEntity(entity.getReservedStorageId()));
        model.setGameId(uuidConverter.convertEntity(entity.getGameId()));
        model.setType(GameItemType.RESERVED_STORAGE);
        model.setExternalReference(uuidConverter.convertEntity(entity.getExternalReference()));
        model.setContainerId(uuidConverter.convertEntity(entity.getContainerId()));
        model.setContainerType(ContainerType.valueOf(entity.getContainerType()));
        model.setDataId(entity.getDataId());
        model.setAmount(entity.getAmount());
        return model;
    }

    @Override
    protected ReservedStorageEntity processDomainConversion(ReservedStorageModel domain) {
        return ReservedStorageEntity.builder()
            .reservedStorageId(uuidConverter.convertDomain(domain.getId()))
            .gameId(uuidConverter.convertDomain(domain.getGameId()))
            .externalReference(uuidConverter.convertDomain(domain.getExternalReference()))
            .containerId(uuidConverter.convertDomain(domain.getContainerId()))
            .containerType(domain.getContainerType().name())
            .dataId(domain.getDataId())
            .amount(domain.getAmount())
            .build();
    }
}
