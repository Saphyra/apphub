package com.github.saphyra.apphub.service.custom.elite_base.dao.body_data.body_material;

import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class BodyMaterialDao extends AbstractDao<BodyMaterialEntity, BodyMaterial, String, BodyMaterialRepository> {
    private final UuidConverter uuidConverter;

    BodyMaterialDao(BodyMaterialConverter converter, BodyMaterialRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public List<BodyMaterial> getByBodyId(UUID bodyId) {
        return converter.convertEntity(repository.getByBodyId(uuidConverter.convertDomain(bodyId)));
    }
}
