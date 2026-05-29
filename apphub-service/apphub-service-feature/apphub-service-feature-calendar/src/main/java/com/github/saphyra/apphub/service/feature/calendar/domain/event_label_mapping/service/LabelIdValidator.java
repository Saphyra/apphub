package com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.service;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.deprecated_dao.DeprecatedLabelDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class LabelIdValidator {
    private final DeprecatedLabelDao labelDao;

    void validate(List<UUID> labels) {
        ValidationUtil.notNull(labels, "labels");
        ValidationUtil.doesNotContainNull(labels, "labels");

        labels.forEach(labelId -> {
            if (!labelDao.existsById(labelId)) {
                throw ExceptionFactory.invalidParam("field", "DeprecatedLabel with id " + labelId + " does not exist");
            }
        });
    }
}
