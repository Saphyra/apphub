package com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class AllocatedResource {
    private final UUID allocatedResourceId;
    private final UUID location;
    private final UUID externalReference;
    private final String dataId;
    private int amount;
}
