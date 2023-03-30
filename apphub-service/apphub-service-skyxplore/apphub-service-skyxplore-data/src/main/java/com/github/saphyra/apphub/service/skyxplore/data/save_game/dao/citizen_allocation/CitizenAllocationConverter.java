package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.citizen_allocation;

import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenAllocationModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class CitizenAllocationConverter extends ConverterBase<CitizenAllocationEntity, CitizenAllocationModel> {
    private final UuidConverter uuidConverter;

    @Override
    protected CitizenAllocationEntity processDomainConversion(CitizenAllocationModel domain) {
        return CitizenAllocationEntity.builder()
            .citizenAllocationId(uuidConverter.convertDomain(domain.getId()))
            .gameId(uuidConverter.convertDomain(domain.getGameId()))
            .citizenId(uuidConverter.convertDomain(domain.getCitizenId()))
            .processId(uuidConverter.convertDomain(domain.getProcessId()))
            .build();
    }

    @Override
    protected CitizenAllocationModel processEntityConversion(CitizenAllocationEntity entity) {
        CitizenAllocationModel model = new CitizenAllocationModel();

        model.setId(uuidConverter.convertEntity(entity.getCitizenAllocationId()));
        model.setGameId(uuidConverter.convertEntity(entity.getGameId()));
        model.setType(GameItemType.CITIZEN_ALLOCATION);
        model.setCitizenId(uuidConverter.convertEntity(entity.getCitizenId()));
        model.setProcessId(uuidConverter.convertEntity(entity.getProcessId()));

        return model;
    }
}
