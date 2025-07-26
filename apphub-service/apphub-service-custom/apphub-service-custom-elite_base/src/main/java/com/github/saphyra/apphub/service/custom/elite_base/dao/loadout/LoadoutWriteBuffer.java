package com.github.saphyra.apphub.service.custom.elite_base.dao.loadout;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.dao.WriteBuffer;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
class LoadoutWriteBuffer extends WriteBuffer<LoadoutDomainId, Loadout> {
    private final LoadoutRepository loadoutRepository;
    private final LoadoutConverter loadoutConverter;

    LoadoutWriteBuffer(DateTimeUtil dateTimeUtil, LoadoutRepository loadoutRepository, LoadoutConverter loadoutConverter) {
        super(dateTimeUtil);
        this.loadoutRepository = loadoutRepository;
        this.loadoutConverter = loadoutConverter;
    }

    @Override
    protected void doSynchronize(Collection<Loadout> bufferCopy) {
        loadoutRepository.saveAll(loadoutConverter.convertDomain(bufferCopy));
    }

    @Override
    protected LoadoutDomainId getDomainId(Loadout loadout) {
        return LoadoutDomainId.builder()
            .externalReference(loadout.getExternalReference())
            .type(loadout.getType())
            .name(loadout.getName())
            .build();
    }
}
