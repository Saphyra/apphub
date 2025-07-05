package com.github.saphyra.apphub.service.custom.elite_base.dao.loadout;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.common_util.dao.DeleteBuffer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class LoadoutDeleteBuffer extends DeleteBuffer<LoadoutDomainId> {
    private final LoadoutRepository loadoutRepository;
    private final UuidConverter uuidConverter;

    protected LoadoutDeleteBuffer(DateTimeUtil dateTimeUtil, LoadoutRepository loadoutRepository, UuidConverter uuidConverter) {
        super(dateTimeUtil);
        this.loadoutRepository = loadoutRepository;
        this.uuidConverter = uuidConverter;

    }

    @Override
    protected void doSynchronize() {
        List<LoadoutEntityId> ids = buffer.stream()
            .map(loadoutDomainId -> LoadoutEntityId.builder()
                .externalReference(uuidConverter.convertDomain(loadoutDomainId.getExternalReference()))
                .type(loadoutDomainId.getType())
                .name(loadoutDomainId.getName())
                .build())
            .toList();

        loadoutRepository.deleteAllById(ids);
    }
}
