package com.github.saphyra.apphub.service.custom.elite_base.dao.loadout;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class LoadoutDomainId {
    private final UUID externalReference;
    private final LoadoutType type;
    private final String name;
}
