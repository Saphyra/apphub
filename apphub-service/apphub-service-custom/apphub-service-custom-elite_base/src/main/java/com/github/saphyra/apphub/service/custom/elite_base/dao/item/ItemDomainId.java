package com.github.saphyra.apphub.service.custom.elite_base.dao.item;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ItemDomainId {
    private final UUID externalReference;
    private final String itemName;
}
