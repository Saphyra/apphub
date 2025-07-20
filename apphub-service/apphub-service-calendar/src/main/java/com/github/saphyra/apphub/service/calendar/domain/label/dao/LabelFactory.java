package com.github.saphyra.apphub.service.calendar.domain.label.dao;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class LabelFactory {
    private final IdGenerator idGenerator;

    public Label create(UUID userId, String label) {
        return Label.builder()
            .labelId(idGenerator.randomUuid())
            .userId(userId)
            .label(label)
            .build();
    }
}
