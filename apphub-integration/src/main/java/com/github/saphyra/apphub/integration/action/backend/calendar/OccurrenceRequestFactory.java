package com.github.saphyra.apphub.integration.action.backend.calendar;

import com.github.saphyra.apphub.integration.structure.api.calendar.OccurrenceRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.OccurrenceStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public class OccurrenceRequestFactory {
    public static final LocalDate DEFAULT_DATE = LocalDate.now().plusDays(3);
    public static final LocalTime DEFAULT_TIME = LocalTime.of(12, 37);
    public static final String DEFAULT_NOTE = "note";
    public static final OccurrenceStatus DEFAULT_STATUS = OccurrenceStatus.PENDING;
    public static final int DEFAULT_REMIND_ME_BEFORE_DAYS = 0;
    public static final boolean DEFAULT_REMINDED = false;
    public static final LocalDate NEW_DATE = LocalDate.now().plusWeeks(5);
    public static final LocalTime NEW_TIME = LocalTime.of(14, 15);
    public static final String NEW_NOTE = "new note";
    public static final OccurrenceStatus NEW_STATUS = OccurrenceStatus.DONE;
    public static final int NEW_REMIND_ME_BEFORE_DAYS = 2;
    public static final boolean NEW_REMINDED = true;

    public static OccurrenceRequest validRequest() {
        return OccurrenceRequest.builder()
            .date(DEFAULT_DATE)
            .time(DEFAULT_TIME)
            .status(DEFAULT_STATUS)
            .note(DEFAULT_NOTE)
            .remindMeBeforeDays(DEFAULT_REMIND_ME_BEFORE_DAYS)
            .reminded(DEFAULT_REMINDED)
            .build();
    }

    public static OccurrenceRequest editRequest() {
        return OccurrenceRequest.builder()
            .date(NEW_DATE)
            .time(NEW_TIME)
            .status(NEW_STATUS)
            .note(NEW_NOTE)
            .remindMeBeforeDays(NEW_REMIND_ME_BEFORE_DAYS)
            .reminded(NEW_REMINDED)
            .build();
    }
}
