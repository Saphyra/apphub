package com.github.saphyra.apphub.service.calendar.domain.label.service;

import com.github.saphyra.apphub.api.calendar.model.response.LabelResponse;
import com.github.saphyra.apphub.service.calendar.domain.event_label_mapping.dao.EventLabelMapping;
import com.github.saphyra.apphub.service.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.calendar.domain.label.dao.LabelDao;
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
    private final LabelMapper labelMapper;

    public List<LabelResponse> getByEventId(UUID eventId) {
        return eventLabelMappingDao.getByEventId(eventId)
            .stream()
            .map(EventLabelMapping::getLabelId)
            .map(this::getLabel)
            .toList();
    }

    public List<LabelResponse> getByUserId(UUID userId) {
        return labelDao.getByUserId(userId)
            .stream()
            .map(labelMapper::toResponse)
            .toList();
    }

    public LabelResponse getLabel(UUID labelId) {
        return labelMapper.toResponse(labelDao.findByIdValidated(labelId));
    }
}
