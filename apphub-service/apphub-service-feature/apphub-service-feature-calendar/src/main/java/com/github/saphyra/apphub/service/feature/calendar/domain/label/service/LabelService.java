package com.github.saphyra.apphub.service.feature.calendar.domain.label.service;

import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.deprecated_dao.DeprecatedEventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.deprecated_dao.DeprecatedLabel;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.deprecated_dao.DeprecatedLabelDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.deprecated_dao.DeprecatedLabelFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class LabelService {
    private final DeprecatedLabelDao labelDao;
    private final DeprecatedLabelFactory labelFactory;
    private final LabelValidator labelValidator;
    private final DeprecatedEventLabelMappingDao eventLabelMappingDao;

    public UUID createLabel(UUID userId, String label) {
        labelValidator.validate(userId, label);

        DeprecatedLabel domain = labelFactory.create(userId, label);
        labelDao.save(domain);

        return domain.getLabelId();
    }

    @Transactional
    public void deleteLabel(UUID userId, UUID labelId) {
        eventLabelMappingDao.deleteByUserIdAndLabelId(userId, labelId);
        labelDao.deleteByUserIdAndLabelId(userId, labelId);
    }

    public void editLabel(UUID userId, UUID labelId, String label) {
        labelValidator.validate(userId, label);

        DeprecatedLabel l = labelDao.findByIdValidated(labelId);
        l.setLabel(label);

        labelDao.save(l);
    }
}
