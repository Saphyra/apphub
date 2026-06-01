package com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.service;

import com.github.saphyra.apphub.service.feature.calendar.common.dao.CommonCalendarDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class EventLabelMappingService {
    private final CommonCalendarDao commonCalendarDao;
    private final EventLabelMappingDao eventLabelMappingDao;
    private final LabelIdValidator labelIdValidator;

    public List<UUID> getLabelIds(UUID userId, UUID eventId) {
        return eventLabelMappingDao.getLabelsOfEvent(userId, eventId);
    }

    public void setLabels(UUID userId, UUID eventId, List<UUID> labels) {
        labelIdValidator.validate(userId, labels);

        commonCalendarDao.editLabelsOfEvent(userId, eventId, labels);
    }
}
