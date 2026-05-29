package com.github.saphyra.apphub.service.feature.calendar.domain.label.deprecated_dao;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Deprecated(forRemoval = true)
public class DeprecatedLabelFactory {
    private final IdGenerator idGenerator;

    public DeprecatedLabel create(UUID userId, String label) {
        return DeprecatedLabel.builder()
            .labelId(idGenerator.randomUuid())
            .userId(userId)
            .label(label)
            .build();
    }
}
