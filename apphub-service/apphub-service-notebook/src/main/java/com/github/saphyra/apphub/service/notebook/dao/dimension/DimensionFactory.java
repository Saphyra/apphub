package com.github.saphyra.apphub.service.notebook.dao.dimension;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class DimensionFactory {
    private final IdGenerator idGenerator;

    public Dimension create(UUID userId, UUID externalReference, Integer index) {
        UUID dimensionId = idGenerator.randomUuid();
        return create(userId, externalReference, index, dimensionId);
    }

    public Dimension create(UUID userId, UUID externalReference, Integer index, UUID dimensionId) {
        return Dimension.builder()
            .dimensionId(dimensionId)
            .userId(userId)
            .externalReference(externalReference)
            .index(index)
            .build();
    }
}
