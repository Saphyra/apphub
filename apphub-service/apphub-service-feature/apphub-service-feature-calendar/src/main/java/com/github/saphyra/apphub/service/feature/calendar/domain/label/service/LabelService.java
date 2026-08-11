package com.github.saphyra.apphub.service.feature.calendar.domain.label.service;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.response.LabelResponse;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.feature.calendar.common.dao.CommonCalendarDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.LabelObjectQueryService;
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
//TODO unit test
public class LabelService {
    private final LabelDao labelDao;
    private final LabelFactory labelFactory;
    private final LabelValidator labelValidator;
    private final CommonCalendarDao commonCalendarDao;
    private final LabelToResponseMapper labelToResponseMapper;
    private final LabelObjectQueryService labelObjectQueryService;

    public LabelResponse createLabel(UUID userId, String label) {
        labelValidator.validate(label);

        Label domain = labelFactory.create(userId, label);
        commonCalendarDao.saveLabel(domain);

        return labelToResponseMapper.toResponse(userId, domain);
    }

    public void deleteLabel(UUID userId, UUID labelId) {
        labelObjectQueryService.findLabel(userId, labelId, Grant.DELETE)
            .ifPresent(label -> commonCalendarDao.deleteLabel(label.getUserId(), label.getLabelId()));
    }

    public void editLabel(UUID userId, UUID labelId, String labelText) {
        labelValidator.validate(labelText);

        Label label = labelObjectQueryService.findLabel(userId, labelId, Grant.VIEW, Grant.EDIT)
            .orElseThrow(() -> ExceptionFactory.notFound("Label %s not found or user %s has no permission to edit it".formatted(labelId, userId)));

        label.setLabel(labelText);

        labelDao.save(label);
    }
}
