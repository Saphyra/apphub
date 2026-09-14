package com.github.saphyra.apphub.service.feature.calendar.domain.label.dao;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LabelFactory {
    private final IdGenerator idGenerator;

    public Label create(UUID userId, String label) {
        return Label.builder()
            .userId(userId)
            .labelId(idGenerator.randomUuid())
            .label(label)
            .build();
    }
}
