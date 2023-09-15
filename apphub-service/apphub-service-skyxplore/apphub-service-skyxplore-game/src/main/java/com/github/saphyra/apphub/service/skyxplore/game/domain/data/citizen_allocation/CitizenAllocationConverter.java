package com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation;

import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenAllocationModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameDataToModelConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class CitizenAllocationConverter implements GameDataToModelConverter {
    public List<CitizenAllocationModel> toModel(UUID gameId, Collection<CitizenAllocation> citizenAllocations) {
        return citizenAllocations.stream()
            .map(citizenAllocation -> toModel(gameId, citizenAllocation))
            .collect(Collectors.toList());
    }

    public CitizenAllocationModel toModel(UUID gameId, CitizenAllocation citizenAllocation) {
        CitizenAllocationModel model = new CitizenAllocationModel();

        model.setId(citizenAllocation.getCitizenAllocationId());
        model.setGameId(gameId);
        model.setType(GameItemType.CITIZEN_ALLOCATION);
        model.setCitizenId(citizenAllocation.getCitizenId());
        model.setProcessId(citizenAllocation.getProcessId());

        return model;
    }

    @Override
    public List<CitizenAllocationModel> convert(UUID gameId, GameData gameData) {
        return toModel(gameId, gameData.getCitizenAllocations());
    }
}
