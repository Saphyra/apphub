package com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class ReservedStorage {
    private final UUID reservedStorageId;
    private final UUID externalReference;
    private final String dataId;
    private int amount;
}
