package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.body_data.body_ring;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class BodyRingSyncService {
    private final BodyRingDao bodyRingDao;

    public void sync(UUID bodyId, List<BodyRing> newRings) {
        List<BodyRing> existingRings = bodyRingDao.getByBodyId(bodyId);

        List<BodyRing> toDelete = existingRings.stream()
            .filter(bodyMaterial -> !newRings.contains(bodyMaterial))
            .toList();
        List<BodyRing> toSave = newRings.stream()
            .filter(bodyMaterial -> !existingRings.contains(bodyMaterial))
            .toList();

        bodyRingDao.deleteAll(toDelete);
        bodyRingDao.saveAll(toSave);
    }
}
