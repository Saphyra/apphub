package com.github.saphyra.apphub.service.feature.calendar.domain.label.service;

import com.github.saphyra.apphub.api.feature.calendar.model.response.LabelResponse;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.deprecated_dao.DeprecatedEventLabelMapping;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.deprecated_dao.DeprecatedEventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.deprecated_dao.DeprecatedLabelDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class LabelQueryService {
    private final DeprecatedEventLabelMappingDao eventLabelMappingDao;
    private final DeprecatedLabelDao labelDao;
    private final LabelMapper labelMapper;

    public List<LabelResponse> getByEventId(UUID eventId) {
        return eventLabelMappingDao.getByEventId(eventId)
            .stream()
            .map(DeprecatedEventLabelMapping::getLabelId)
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
