package com.github.saphyra.apphub.service.calendar.service;

import com.github.saphyra.apphub.api.calendar.model.ReferenceDate;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ReferenceDateValidator {
    public void validate(ReferenceDate referenceDate) {
        ValidationUtil.notNull(referenceDate, "referenceDate");
        ValidationUtil.notNull(referenceDate.getMonth(), "referenceDate.month");
        ValidationUtil.notNull(referenceDate.getDay(), "referenceDate.day");
    }
}
