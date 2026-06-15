package com.github.saphyra.apphub.service.feature.elite_base.service.powerplay.merit_farm.mining.nakato_kaine;

import com.github.saphyra.apphub.api.feature.elite_base.model.ReserveLevel;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.Body;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.BodyDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.BodyType;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.body_data.BodyData;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.body_data.BodyDataDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.body_ring.BodyRing;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.body_ring.BodyRingDao;
import com.github.saphyra.apphub.service.feature.elite_base.dao.body.body_ring.RingType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class MetallicRingService {
    private final BodyDao bodyDao;
    private final BodyRingDao bodyRingDao;
    private final BodyDataDao bodyDataDao;

    Map<UUID, ReserveLevel> getSystemsWithMetallicRing(Collection<UUID> starSystemIds) {
        log.info("Get bodies of {} starSystems", starSystemIds.size());
        Map<UUID, List<UUID>> starSystemBodiesMap = bodyDao.getByStarSystemIds(starSystemIds)
            .stream()
            .filter(body -> body.getType() == BodyType.PLANET)
            .collect(Collectors.groupingBy(Body::getStarSystemId))
            .entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream().map(Body::getId).toList()));

        List<UUID> bodyIds = starSystemBodiesMap.values()
            .stream()
            .flatMap(Collection::stream)
            .distinct()
            .toList();
        log.info("Bodies found: {}", bodyIds.size());

        List<BodyRing> metallicRings = bodyRingDao.getByBodyIds(bodyIds)
            .stream()
            .filter(bodyRing -> bodyRing.getType() == RingType.METALLIC)
            .toList();
        log.info("Metallic rings found: {}", metallicRings.size());

        Map<UUID, ReserveLevel> bodyReserveLevelMap = getReserveLevels(metallicRings.stream().map(BodyRing::getBodyId).collect(Collectors.toSet()));

        return metallicRings.stream()
            .map(BodyRing::getBodyId)
            .distinct()
            .map(bodyId -> starSystemBodiesMap.entrySet()
                .stream()
                .filter(e -> e.getValue().contains(bodyId))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("StarSystemId not found for bodyId" + bodyId))
            )
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue()
                    .stream()
                    /*
                    bodyReserveLevelMap contains only reserveLevel of metallic rings,
                    while starSystemBodiesMap contains all the bodies of a StarSystem.
                     */
                    .map(bodyId -> bodyReserveLevelMap.getOrDefault(bodyId, ReserveLevel.UNKNOWN))
                    .max(Comparator.comparingInt(ReserveLevel::getLevel))
                    .orElse(ReserveLevel.UNKNOWN),
                //Planet might have multiple metallic rings, if this happens, pick the ring with higher reserve level (in reality, reserve level should be equal for all rings in the same system)
                (r1, r2) -> r1.getLevel() >= r2.getLevel() ? r1 : r2
            ));
    }

    private Map<UUID, ReserveLevel> getReserveLevels(Collection<UUID> bodyIds) {
        return bodyDataDao.getByIds(bodyIds)
            .stream()
            .collect(Collectors.toMap(BodyData::getBodyId, bodyData -> Optional.ofNullable(bodyData.getReserveLevel()).orElse(ReserveLevel.UNKNOWN)));
    }
}
