package com.github.saphyra.apphub.service.feature.calendar.domain.label.service;

import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.feature.calendar.common.dao.CommonCalendarDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.Label;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.LabelDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.LabelFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class LabelService {
    private final LabelDao labelDao;
    private final LabelFactory labelFactory;
    private final LabelValidator labelValidator;
    private final CommonCalendarDao commonCalendarDao;

    public UUID createLabel(UUID userId, String label) {
        labelValidator.validate(userId, label);

        Label domain = labelFactory.create(label);
        commonCalendarDao.saveLabel(userId, domain);

        return domain.getLabelId();
    }

    public void deleteLabel(UUID userId, UUID labelId) {
        commonCalendarDao.deleteLabel(userId, labelId);
    }

    public void editLabel(UUID userId, UUID labelId, String label) {
        Label domain = labelValidator.validate(userId, label)
            .stream()
            .filter(l -> l.getLabelId().equals(labelId))
            .findAny()
            .orElseThrow(() -> ExceptionFactory.notFound("Label not found by id " + labelId));

        domain.setLabel(label);

        labelDao.save(userId, domain);
    }
}
