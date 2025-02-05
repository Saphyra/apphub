package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.body_data;

import com.github.saphyra.apphub.lib.common_util.DateTimeConverter;
import com.github.saphyra.apphub.lib.common_util.LazyLoadedField;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.body_data.body_material.BodyMaterialDao;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.body_data.body_material.BodyMaterialSyncService;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.body_data.body_ring.BodyRingDao;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.body_data.body_ring.BodyRingSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.UUID;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Component
@RequiredArgsConstructor
@Slf4j
class BodyDataConverter extends ConverterBase<BodyDataEntity, BodyData> {
    private final UuidConverter uuidConverter;
    private final DateTimeConverter dateTimeConverter;
    private final BodyMaterialDao bodyMaterialDao;
    private final BodyRingDao bodyRingDao;
    private final BodyMaterialSyncService bodyMaterialSyncService;
    private final BodyRingSyncService bodyRingSyncService;

    @Override
    protected BodyDataEntity processDomainConversion(BodyData domain) {
        if (isTrue(domain.getLandable())) {
            bodyMaterialSyncService.sync(domain.getBodyId(), domain.getMaterials());
        }

        if (isTrue(domain.getHasRing())) {
            bodyRingSyncService.sync(domain.getBodyId(), domain.getRings());
        }

        return BodyDataEntity.builder()
            .bodyId(uuidConverter.convertDomain(domain.getBodyId()))
            .lastUpdate(dateTimeConverter.convertDomain(domain.getLastUpdate()))
            .landable(domain.getLandable())
            .surfaceGravity(domain.getSurfaceGravity())
            .reserveLevel(domain.getReserveLevel())
            .hasRing(domain.getHasRing())
            .build();
    }

    @Override
    protected BodyData processEntityConversion(BodyDataEntity entity) {
        UUID bodyId = uuidConverter.convertEntity(entity.getBodyId());
        return BodyData.builder()
            .bodyId(bodyId)
            .lastUpdate(dateTimeConverter.convertToLocalDateTime(entity.getLastUpdate()))
            .landable(entity.getLandable())
            .surfaceGravity(entity.getSurfaceGravity())
            .reserveLevel(entity.getReserveLevel())
            .hasRing(entity.getHasRing())
            .materials(new LazyLoadedField<>(() -> isTrue(entity.getLandable()) ? bodyMaterialDao.getByBodyId(bodyId) : Collections.emptyList()))
            .rings(new LazyLoadedField<>(() -> isTrue(entity.getHasRing()) ? bodyRingDao.getByBodyId(bodyId) : Collections.emptyList()))
            .build();
    }
}
