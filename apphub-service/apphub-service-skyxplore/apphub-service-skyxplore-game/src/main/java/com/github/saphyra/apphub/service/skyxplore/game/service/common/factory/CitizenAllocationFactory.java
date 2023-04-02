package com.github.saphyra.apphub.service.skyxplore.game.service.common.factory;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CitizenAllocationFactory {
    private final IdGenerator idGenerator;

    public CitizenAllocation create(UUID citizenId, UUID processId) {
        return CitizenAllocation.builder()
            .citizenAllocationId(idGenerator.randomUuid())
            .citizenId(citizenId)
            .processId(processId)
            .build();
    }
}
