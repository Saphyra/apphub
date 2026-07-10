package com.github.saphyra.apphub.service.feature.calendar.domain.label.service;

import com.github.saphyra.apphub.api.feature.calendar.model.response.LabelResponse;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.Label;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.LabelDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class LabelQueryService {
    private final EventLabelMappingDao eventLabelMappingDao;
    private final LabelDao labelDao;
    private final LabelToResponseMapper labelToResponseMapper;

    public List<LabelResponse> getByEventId(UUID userId, UUID eventId) {
        List<UUID> labelIds = eventLabelMappingDao.getLabelsOfEvent(userId, eventId)
            .stream()
            .toList();
        List<Label> labels = labelDao.getByLabelIds(userId, labelIds);
        return labelToResponseMapper.toResponse(labels);
    }

    public List<LabelResponse> getByUserId(UUID userId) {
        return labelDao.getByUserId(userId)
            .stream()
            .map(labelToResponseMapper::toResponse)
            .toList();
    }

    public LabelResponse getLabel(UUID userId, UUID labelId) {
        return labelToResponseMapper.toResponse(labelDao.findByIdValidated(userId, labelId));
    }
}
