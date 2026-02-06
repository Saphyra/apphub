package com.github.saphyra.apphub.service.custom.elite_base.dao.item.loadout.spaceship;

import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemLocationType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.item.loadout.Loadout;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
public class Spaceship implements Loadout {
    private final UUID externalReference;
    private final String itemName;
    private final ItemLocationType locationType;
    private final Long marketId;
    private final UUID starSystemId;
}
