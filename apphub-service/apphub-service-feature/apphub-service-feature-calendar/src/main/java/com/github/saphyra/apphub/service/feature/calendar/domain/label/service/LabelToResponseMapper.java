package com.github.saphyra.apphub.service.feature.calendar.domain.label.service;

import com.github.saphyra.apphub.api.feature.calendar.model.response.LabelResponse;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.Label;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class LabelToResponseMapper {
    LabelResponse toResponse(UUID userId, Label label) {
        return LabelResponse.builder()
            .labelId(label.getLabelId())
            .userId(label.getUserId())
            .label(label.getLabel())
            .shared(!userId.equals(label.getUserId()))
            .build();
    }

    public List<LabelResponse> toResponse(UUID userId, List<Label> labels) {
        return labels.stream()
            .map(label -> toResponse(userId, label))
            .toList();
    }
}
