package com.github.saphyra.apphub.service.feature.calendar.domain.label.dao;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
//TODO unit test
public class LabelFactory {
    private final IdGenerator idGenerator;

    public Label create(String label) {
        return Label.builder()
            .labelId(idGenerator.randomUuid())
            .label(label)
            .build();
    }
}
