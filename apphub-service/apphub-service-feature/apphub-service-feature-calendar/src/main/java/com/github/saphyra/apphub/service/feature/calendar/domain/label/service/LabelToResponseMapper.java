package com.github.saphyra.apphub.service.feature.calendar.domain.label.service;

import com.github.saphyra.apphub.api.feature.calendar.model.response.LabelResponse;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.Label;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
class LabelToResponseMapper {
    LabelResponse toResponse(Label label) {
        return LabelResponse.builder()
            .labelId(label.getLabelId())
            .label(label.getLabel())
            .build();
    }

    public List<LabelResponse> toResponse(List<Label> labels) {
        return labels.stream()
            .map(this::toResponse)
            .toList();
    }
}
