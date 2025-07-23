package com.github.saphyra.apphub.service.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.api.calendar.model.request.OccurrenceRequest;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.calendar.domain.occurrence.dao.OccurrenceDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class EditOccurrenceService {
    private final OccurrenceRequestValidator occurrenceRequestValidator;
    private final OccurrenceDao occurrenceDao;

    public void editOccurrence(UUID occurrenceId, OccurrenceRequest request) {
        occurrenceRequestValidator.validate(request);

        Occurrence occurrence = occurrenceDao.findByIdValidated(occurrenceId);
        occurrence.setDate(request.getDate());
        occurrence.setTime(request.getTime());
        occurrence.setStatus(request.getStatus());
        occurrence.setNote(request.getNote());
        occurrence.setRemindMeBeforeDays(request.getRemindMeBeforeDays());

        occurrenceDao.save(occurrence);
    }
}
