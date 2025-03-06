package com.github.saphyra.apphub.service.custom.elite_base.dao.body;

import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Component
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

    //TODO unit test
    public List<Body> findAllById(List<UUID> bodyIds) {
        List<BodyEntity> entities = StreamSupport.stream(repository.findAllById(bodyIds.stream().map(uuidConverter::convertDomain).toList()).spliterator(), false)
            .toList();

        return converter.convertEntity(entities);
    }
}
