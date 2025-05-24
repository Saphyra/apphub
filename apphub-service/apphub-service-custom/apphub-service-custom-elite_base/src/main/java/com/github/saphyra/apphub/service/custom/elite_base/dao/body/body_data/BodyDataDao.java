package com.github.saphyra.apphub.service.custom.elite_base.dao.body.body_data;

import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class BodyDataDao extends AbstractDao<BodyDataEntity, BodyData, String, BodyDataRepository> {
    private final UuidConverter uuidConverter;

    BodyDataDao(BodyDataConverter converter, BodyDataRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public Optional<BodyData> findById(UUID id) {
        return findById(uuidConverter.convertDomain(id));
    }
}
