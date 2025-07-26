package com.github.saphyra.apphub.service.calendar.domain.label.service;

import com.github.saphyra.apphub.service.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.calendar.domain.label.dao.Label;
import com.github.saphyra.apphub.service.calendar.domain.label.dao.LabelDao;
import com.github.saphyra.apphub.service.calendar.domain.label.dao.LabelFactory;
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
    private final EventLabelMappingDao eventLabelMappingDao;

    public void createLabel(UUID userId, String label) {
        labelValidator.validate(userId, label);

        labelDao.save(labelFactory.create(userId, label));
    }

    public void deleteLabel(UUID userId, UUID labelId) {
        eventLabelMappingDao.deleteByUserIdAndLabelId(userId, labelId);
        labelDao.deleteByUserIdAndLabelId(userId, labelId);
    }

    public void editLabel(UUID userId, UUID labelId, String label) {
        labelValidator.validate(userId, label);

        Label l = labelDao.findByIdValidated(labelId);
        l.setLabel(label);

        labelDao.save(l);
    }
}
