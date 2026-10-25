package com.github.saphyra.apphub.service.feature.elite_base.processor.dao.body.body_ring;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class BodyRingSyncService {
    private final BodyRingDao bodyRingDao;

    public void sync(UUID bodyId, List<BodyRing> newRings) {
        Map<String, BodyRing> existingRings = new HashMap<>();

        for (BodyRing ring : bodyRingDao.getByBodyId(bodyId)) {
            if (existingRings.containsKey(ring.getName())) {
                log.info("Deleting duplicated ring: {}", ring);
                bodyRingDao.delete(ring);
            } else {
                existingRings.put(ring.getName(), ring);
            }
        }

        List<String> newRingNames = newRings.stream()
            .map(BodyRing::getName)
            .toList();

        List<BodyRing> toDelete = existingRings.values()
            .stream()
            .filter(ring -> !newRingNames.contains(ring.getName()))
            .toList();
        List<BodyRing> toSave = newRings.stream()
            .filter(ring -> !existingRings.containsKey(ring.getName()))
            .toList();

        bodyRingDao.deleteAll(toDelete);
        bodyRingDao.saveAll(toSave);
    }
}
