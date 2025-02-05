package com.github.saphyra.apphub.service.elite_base.message_processing.dao.loadout;

import com.github.saphyra.apphub.service.elite_base.message_processing.dao.last_update.EntityType;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.last_update.EntityTypeProvider;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum LoadoutType implements EntityTypeProvider {
    OUTFITTING(EntityType.SHIP_MODULE),
    SHIPYARD(EntityType.SHIP),
    ;

    private final EntityType entityType;

    @Override
    public EntityType get() {
        return entityType;
    }
}
