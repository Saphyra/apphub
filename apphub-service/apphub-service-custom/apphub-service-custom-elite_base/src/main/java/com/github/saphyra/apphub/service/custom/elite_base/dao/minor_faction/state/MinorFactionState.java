package com.github.saphyra.apphub.service.custom.elite_base.dao.minor_faction.state;

import com.github.saphyra.apphub.service.custom.elite_base.dao.FactionStateEnum;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
public class MinorFactionState {
    private final UUID minorFactionId;
    private final StateStatus status;
    private final FactionStateEnum state;
    private final Integer trend;
}
