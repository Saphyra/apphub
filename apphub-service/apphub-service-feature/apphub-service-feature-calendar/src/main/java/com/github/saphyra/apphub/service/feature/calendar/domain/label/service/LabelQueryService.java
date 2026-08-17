package com.github.saphyra.apphub.service.feature.calendar.domain.label.service;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.response.LabelResponse;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.Label;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class LabelQueryService {
    private final LabelToResponseMapper labelToResponseMapper;
    private final LabelObjectQueryService labelObjectQueryService;

    public List<LabelResponse> getByEventId(UUID userId, UUID eventId) {
        List<Label> labels = labelObjectQueryService.getLabelsOfEvent(userId, eventId);
        return labelToResponseMapper.toResponse(userId, labels);
    }

    public List<LabelResponse> getByUserId(UUID userId) {
        return labelObjectQueryService.getByUserId(userId)
            .map(label -> labelToResponseMapper.toResponse(userId, label))
            .toList();
    }

    public LabelResponse getLabel(UUID userId, UUID labelId) {
        return labelObjectQueryService.findLabel(userId, labelId , Grant.VIEW)
            .map(label -> labelToResponseMapper.toResponse(userId, label))
            .orElseThrow(() -> ExceptionFactory.notFound(labelId + " label not found or not accessible by user " + userId));
    }
}
