package com.github.saphyra.apphub.service.feature.calendar.domain.label.service;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class LabelValidator {
    public void validate(String label) {
        ValidationUtil.notBlank(label, "label");
        ValidationUtil.maxLength(label, 255, "label");
    }
}
