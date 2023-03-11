package com.github.saphyra.apphub.service.calendar.service.occurrence.service.edit;

import com.github.saphyra.apphub.api.calendar.model.EditOccurrenceRequest;
import com.github.saphyra.apphub.service.calendar.service.ReferenceDateValidator;
import com.github.saphyra.apphub.service.calendar.service.event.service.EventTitleValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class EditOccurrenceRequestValidator {
    private final EventTitleValidator eventTitleValidator;
    private final ReferenceDateValidator referenceDateValidator;

    void validate(EditOccurrenceRequest request) {
        referenceDateValidator.validate(request.getReferenceDate());
        eventTitleValidator.validate(request.getTitle());
    }
}
