package com.github.saphyra.apphub.service.calendar.domain.label.service;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.calendar.domain.label.dao.LabelDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class LabelValidator {
    private final LabelDao labelDao;

    public void validate(UUID userId, String label) {
        ValidationUtil.notBlank(label, "label");
        ValidationUtil.maxLength(label, 255, "label");

        if (labelDao.getByUserId(userId).stream().anyMatch(existingLabel -> existingLabel.getLabel().equals(label))) {
            throw ExceptionFactory.invalidParam("label", "already exists");
        }
    }
}
