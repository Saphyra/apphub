package com.github.saphyra.apphub.service.elite_base.dao.body_data.body_material;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class BodyMaterialSyncService {
    private final BodyMaterialDao bodyMaterialDao;

    public void sync(UUID bodyId, List<BodyMaterial> newMaterials) {
        List<BodyMaterial> existingMaterials = bodyMaterialDao.getByBodyId(bodyId);

        List<BodyMaterial> toDelete = existingMaterials.stream()
            .filter(bodyMaterial -> !newMaterials.contains(bodyMaterial))
            .toList();
        List<BodyMaterial> toSave = newMaterials.stream()
            .filter(bodyMaterial -> !existingMaterials.contains(bodyMaterial))
            .toList();

        bodyMaterialDao.deleteAll(toDelete);
        bodyMaterialDao.saveAll(toSave);
    }
}
