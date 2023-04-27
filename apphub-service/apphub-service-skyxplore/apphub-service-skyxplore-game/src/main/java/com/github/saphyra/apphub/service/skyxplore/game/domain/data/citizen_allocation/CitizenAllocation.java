package com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class CitizenAllocation {
    private final UUID citizenAllocationId;
    private final UUID citizenId;
    private final UUID processId;
}
