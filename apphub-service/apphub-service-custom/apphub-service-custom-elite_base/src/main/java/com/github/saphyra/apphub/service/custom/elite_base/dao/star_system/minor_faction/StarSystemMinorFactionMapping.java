package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.minor_faction;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
public class StarSystemMinorFactionMapping {
    private final UUID starSystemId;
    private final UUID minorFactionId;
}
