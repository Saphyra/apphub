package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.common_util.dao.DeleteBuffer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
class StarSystemDeleteBuffer extends DeleteBuffer<UUID> {
    private final StarSystemRepository starSystemRepository;
    private final UuidConverter uuidConverter;

    StarSystemDeleteBuffer(DateTimeUtil dateTimeUtil, StarSystemRepository starSystemRepository, UuidConverter uuidConverter) {
        super(dateTimeUtil);
        this.starSystemRepository = starSystemRepository;
        this.uuidConverter = uuidConverter;
    }

    @Override
    protected void doSynchronize() {
        List<String> ids = buffer.stream()
            .map(uuidConverter::convertDomain)
            .toList();

        starSystemRepository.deleteAllById(ids);
    }
}
