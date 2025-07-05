package com.github.saphyra.apphub.service.custom.elite_base.dao.loadout;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.dao.WriteBuffer;
import org.springframework.stereotype.Component;

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
    protected void doSynchronize() {
        loadoutRepository.saveAll(loadoutConverter.convertDomain(buffer.values()));
    }
}
