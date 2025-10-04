package com.github.saphyra.apphub.service.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.api.calendar.model.request.OccurrenceRequest;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class OccurrenceRequestValidator {
    void validate(OccurrenceRequest request) {
        ValidationUtil.notNull(request.getDate(), "date");
        ValidationUtil.notNull(request.getStatus(), "status");
        ValidationUtil.atLeast(request.getRemindMeBeforeDays(), 0, "remindMeBeforeDays");
        ValidationUtil.notNull(request.getNote(), "note");
        ValidationUtil.notNull(request.getReminded(), "reminded");
    }
}
