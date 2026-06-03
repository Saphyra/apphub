package com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.service;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
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
class LabelIdValidator {
    private final LabelDao labelDao;

    void validate(UUID userId, List<UUID> labels) {
        ValidationUtil.notNull(labels, "labels");
        ValidationUtil.doesNotContainNull(labels, "labels");

        ValidationUtil.notNull(labels, "labels");
        ValidationUtil.containsAll(labels, () -> labelDao.getByLabelIds(userId, labels).stream().map(Label::getLabelId).toList(), "labels");
    }
}
