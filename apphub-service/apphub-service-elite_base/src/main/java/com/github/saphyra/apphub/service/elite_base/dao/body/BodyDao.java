package com.github.saphyra.apphub.service.elite_base.dao.body;

import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
//TODO unit test
public class BodyDao extends AbstractDao<BodyEntity, Body, String, BodyRepository> {
    private final UuidConverter uuidConverter;

    public BodyDao(BodyConverter converter, BodyRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public Optional<Body> findByStarSystemIdAndBodyId(UUID starSystemId, Long bodyId) {
        return converter.convertEntity(repository.findByStarSystemIdAndBodyId(uuidConverter.convertDomain(starSystemId), bodyId));
    }

    public Optional<Body> findByBodyName(String bodyName) {
        return converter.convertEntity(repository.findByBodyName(bodyName));
    }
}
