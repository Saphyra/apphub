package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.commodity;

import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.last_update.EntityType;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.last_update.EntityTypeProvider;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CommodityType implements EntityTypeProvider {
    COMMODITY(EntityType.COMMODITY),
    FC_MATERIAL(EntityType.FC_MATERIAL),
    ;

    private final EntityType entityType;

    @Override
    public EntityType get() {
        return entityType;
    }
}
