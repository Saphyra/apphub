package com.github.saphyra.apphub.service.calendar.domain.label.service;

import com.github.saphyra.apphub.api.calendar.model.response.LabelResponse;
import com.github.saphyra.apphub.service.calendar.domain.label.dao.LabelDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class LabelQueryService {
    private final LabelDao labelDao;
    private final LabelConverter labelConverter;

    public List<LabelResponse> getByEventId(UUID eventId) {
        return labelDao.getByEventId(eventId)
            .stream()
            .map(labelConverter::toResponse)
            .toList();
    }

    public List<LabelResponse> getByUserId(UUID userId) {
        return labelDao.getByUserId(userId)
            .stream()
            .map(labelConverter::toResponse)
            .toList();
    }
}
