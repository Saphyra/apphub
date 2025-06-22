package com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CitizenAllocationFactory {
    private final IdGenerator idGenerator;
    private final CitizenAllocationConverter citizenAllocationConverter;

    public CitizenAllocation save(GameProgressDiff progressDiff, GameData gameData, UUID citizenId, UUID processId) {
        CitizenAllocation citizenAllocation = create(citizenId, processId);

        gameData.getCitizenAllocations()
            .add(citizenAllocation);
        progressDiff.save(citizenAllocationConverter.toModel(gameData.getGameId(), citizenAllocation));

        return citizenAllocation;
    }

    public CitizenAllocation create(UUID citizenId, UUID processId) {
        return CitizenAllocation.builder()
            .citizenAllocationId(idGenerator.randomUuid())
            .citizenId(citizenId)
            .processId(processId)
            .build();
    }
}
