package com.github.saphyra.apphub.service.feature.calendar.domain.label.service;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
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
class LabelValidator {
    private final LabelDao labelDao;

    public List<Label> validate(UUID userId, String label) {
        ValidationUtil.notBlank(label, "label");
        ValidationUtil.maxLength(label, 255, "label");

        List<Label> labels = labelDao.getByUserId(userId);
        if (labels.stream().anyMatch(existingLabel -> existingLabel.getLabel().equals(label))) {
            throw ExceptionFactory.invalidParam("label", "already exists");
        }
        return labels;
    }
}
