package com.github.saphyra.apphub.service.diary.service;

import com.github.saphyra.apphub.api.diary.model.ReferenceDate;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
//TODO unit test
public class ReferenceDateValidator {
    public void validate(ReferenceDate referenceDate) {
        ValidationUtil.notNull(referenceDate, "referenceDate");
        ValidationUtil.notNull(referenceDate.getMonth(), "month");
        ValidationUtil.notNull(referenceDate.getDay(), "day");
    }
}
