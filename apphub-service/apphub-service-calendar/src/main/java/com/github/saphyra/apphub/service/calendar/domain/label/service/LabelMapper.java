package com.github.saphyra.apphub.service.calendar.domain.label.service;

import com.github.saphyra.apphub.api.calendar.model.response.LabelResponse;
import com.github.saphyra.apphub.service.calendar.domain.label.dao.Label;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class LabelMapper {
    LabelResponse toResponse(Label label) {
        return LabelResponse.builder()
            .labelId(label.getLabelId())
            .label(label.getLabel())
            .build();
    }
}
