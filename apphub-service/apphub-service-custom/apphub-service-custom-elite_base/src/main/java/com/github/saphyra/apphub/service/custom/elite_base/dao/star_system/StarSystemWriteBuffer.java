package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.dao.WriteBuffer;
import com.google.common.cache.Cache;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
class StarSystemWriteBuffer extends WriteBuffer<UUID, StarSystem> {
    private final StarSystemRepository starSystemRepository;
    private final StarSystemConverter starSystemConverter;

    protected StarSystemWriteBuffer(DateTimeUtil dateTimeUtil, Cache<UUID, StarSystem> starSystemReadCache, StarSystemRepository starSystemRepository, StarSystemConverter starSystemConverter) {
        super(dateTimeUtil, starSystemReadCache);
        this.starSystemRepository = starSystemRepository;
        this.starSystemConverter = starSystemConverter;
    }

    @Override
    protected void doSynchronize() {
        starSystemRepository.saveAll(starSystemConverter.convertDomain(buffer.values()));
    }
}
