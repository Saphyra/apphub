package com.github.saphyra.apphub.service.elite_base.dao.body_data.body_ring;

import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
//TODO unit test
public class BodyRingDao extends AbstractDao<BodyRingEntity, BodyRing, String, BodyRingRepository> {
    private final UuidConverter uuidConverter;

    BodyRingDao(BodyRingConverter converter, BodyRingRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public List<BodyRing> getByBodyId(UUID bodyId) {
        return converter.convertEntity(repository.getByBodyId(uuidConverter.convertDomain(bodyId)));
    }
}
