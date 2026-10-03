package com.github.saphyra.apphub.service.feature.elite_base.message_processing.saver;

import com.github.saphyra.apphub.api.feature.elite_base.model.ReserveLevel;
import com.github.saphyra.apphub.lib.common_util.LazyLoadedField;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.feature.elite_base.dao.ObjectType;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.body_data.BodyData;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.body_data.BodyDataDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.body_data.BodyDataFactory;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.body_material.BodyMaterialFactory;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.body_ring.BodyRingFactory;
import com.github.saphyra.apphub.service.feature.elite_base.dao.last_update.LastUpdateDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.last_update.LastUpdateFactory;
import com.github.saphyra.apphub.service.feature.elite_base.message_processing.structure.journal.NamePercentPair;
import com.github.saphyra.apphub.service.feature.elite_base.message_processing.structure.journal.Ring;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Component
@RequiredArgsConstructor
@Slf4j
public class BodyDataSaver {
    private final BodyDataDao bodyDataDao;
    private final BodyDataFactory bodyDataFactory;
    private final BodyRingFactory bodyRingFactory;
    private final BodyMaterialFactory bodyMaterialFactory;
    private final LastUpdateDao lastUpdateDao;
    private final LastUpdateFactory lastUpdateFactory;

    public synchronized void save(UUID bodyId, LocalDateTime timestamp, Boolean landable, Double surfaceGravity, ReserveLevel reserveLevel, Boolean hasRing, NamePercentPair[] materials, Ring[] rings) {
        log.debug("Saving bodyData for body {}", bodyId);

        Optional<BodyData> maybeBodyData = bodyDataDao.findById(bodyId);
        if (maybeBodyData.isPresent()) {
            BodyData bodyData = maybeBodyData.get();

            updateFields(timestamp, bodyData, landable, surfaceGravity, reserveLevel, hasRing, materials, rings);
        } else {
            BodyData created = bodyDataFactory.create(bodyId, landable, surfaceGravity, reserveLevel, hasRing, materials, rings);
            log.debug("Created: {}", created);
            bodyDataDao.save(created);

            saveLastUpdate(timestamp, created);
        }

        log.debug("Saved BodyData for body {}", bodyId);
    }

    private void updateFields(LocalDateTime timestamp, BodyData bodyData, Boolean landable, Double surfaceGravity, ReserveLevel reserveLevel, Boolean hasRing, NamePercentPair[] materials, Ring[] rings) {
        LocalDateTime lastUpdated = lastUpdateDao.findByIdOrDefault(bodyData.getBodyId(), ObjectType.BODY_DATA).getLastUpdate();
        if (timestamp.isBefore(lastUpdated)) {
            log.debug("BodyData {} has newer data than {}", bodyData.getBodyId(), timestamp);
            return;
        }

        saveLastUpdate(timestamp, bodyData);

        List<Boolean> results = Stream.of(
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
            .map(UpdateHelper::modify)
            .toList();

        if (results.stream().anyMatch(Boolean::booleanValue)) {
            bodyDataDao.save(bodyData);
        }
    }

    private void saveLastUpdate(LocalDateTime timestamp, BodyData bodyData) {
        lastUpdateDao.save(lastUpdateFactory.create(bodyData.getBodyId(), ObjectType.BODY_DATA, timestamp));
    }
}
