package com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver;

import com.github.saphyra.apphub.lib.common_util.LazyLoadedField;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body_data.BodyData;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body_data.BodyDataDao;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body_data.BodyDataFactory;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body_data.ReserveLevel;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body_data.body_material.BodyMaterialFactory;
import com.github.saphyra.apphub.service.custom.elite_base.dao.body_data.body_ring.BodyRingFactory;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.journal.NamePercentPair;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.journal.Ring;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Component
@RequiredArgsConstructor
@Slf4j
public class BodyDataSaver {
    private final BodyDataDao bodyDataDao;
    private final BodyDataFactory bodyDataFactory;
    private final BodyRingFactory bodyRingFactory;
    private final BodyMaterialFactory bodyMaterialFactory;

    public synchronized void save(UUID bodyId, LocalDateTime timestamp, Boolean landable, Double surfaceGravity, ReserveLevel reserveLevel, Boolean hasRing, NamePercentPair[] materials, Ring[] rings) {
        log.info("Saving bodyData for body {}", bodyId);

        BodyData bodyData = bodyDataDao.findById(bodyId)
            .orElseGet(() -> {
                BodyData created = bodyDataFactory.create(bodyId, timestamp, landable, surfaceGravity, reserveLevel, hasRing, materials, rings);
                log.debug("Created: {}", created);
                bodyDataDao.save(created);
                return created;
            });

        updateFields(timestamp, bodyData, landable, surfaceGravity, reserveLevel, hasRing, materials, rings);

        log.info("Saved BodyData for body {}", bodyId);
    }

    private void updateFields(LocalDateTime timestamp, BodyData bodyData, Boolean landable, Double surfaceGravity, ReserveLevel reserveLevel, Boolean hasRing, NamePercentPair[] materials, Ring[] rings) {
        if (timestamp.isBefore(bodyData.getLastUpdate())) {
            log.debug("BodyData {} has newer data than {}", bodyData.getBodyId(), timestamp);
            return;
        }

        List.of(
                new UpdateHelper(timestamp, bodyData::getLastUpdate, () -> bodyData.setLastUpdate(timestamp)),
                new UpdateHelper(landable, bodyData::getLandable, () -> bodyData.setLandable(landable)),
                new UpdateHelper(surfaceGravity, bodyData::getSurfaceGravity, () -> bodyData.setSurfaceGravity(surfaceGravity)),
                new UpdateHelper(reserveLevel, bodyData::getReserveLevel, () -> bodyData.setReserveLevel(reserveLevel)),
                new UpdateHelper(hasRing, bodyData::getHasRing, () -> bodyData.setHasRing(hasRing)),
                new UpdateHelper(
                    () -> !isTrue(landable) || Objects.equals(CollectionUtils.size(materials), CollectionUtils.size(bodyData.getMaterials())),
                    () -> bodyData.setMaterials(LazyLoadedField.loaded(bodyMaterialFactory.create(bodyData.getBodyId(), materials)))
                ),
                new UpdateHelper(
                    () -> !isTrue(hasRing) || Objects.equals(CollectionUtils.size(rings), CollectionUtils.size(bodyData.getRings())),
                    () -> bodyData.setRings(LazyLoadedField.loaded(bodyRingFactory.create(bodyData.getBodyId(), rings)))
                )
            )
            .forEach(UpdateHelper::modify);

        bodyDataDao.save(bodyData);
    }
}
