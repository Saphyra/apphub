package com.github.saphyra.apphub.service.elite_base.message_processing.dao.body_data;

import com.github.saphyra.apphub.lib.common_util.LazyLoadedField;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.body_data.body_material.BodyMaterialFactory;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.body_data.body_ring.BodyRingFactory;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.NamePercentPair;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.Ring;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Component
@RequiredArgsConstructor
@Slf4j
public class BodyDataFactory {
    private final BodyMaterialFactory bodyMaterialFactory;
    private final BodyRingFactory bodyRingFactory;

    public BodyData create(UUID bodyId, LocalDateTime timestamp, Boolean landable, Double surfaceGravity, ReserveLevel reserveLevel, Boolean hasRing, NamePercentPair[] materials, Ring[] rings) {
        return BodyData.builder()
            .bodyId(bodyId)
            .lastUpdate(timestamp)
            .landable(landable)
            .surfaceGravity(surfaceGravity)
            .reserveLevel(reserveLevel)
            .hasRing(hasRing)
            .materials(LazyLoadedField.loaded(isTrue(landable) ? bodyMaterialFactory.create(bodyId, materials) : Collections.emptyList()))
            .rings(LazyLoadedField.loaded(isTrue(hasRing) ? bodyRingFactory.create(bodyId, rings) : Collections.emptyList()))
            .build();
    }
}
