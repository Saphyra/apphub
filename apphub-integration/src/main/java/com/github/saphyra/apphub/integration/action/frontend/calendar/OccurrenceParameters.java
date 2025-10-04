package com.github.saphyra.apphub.integration.action.frontend.calendar;

import com.github.saphyra.apphub.integration.structure.api.calendar.OccurrenceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class OccurrenceParameters {
    private LocalDate date;
    private LocalTime time;
    private OccurrenceStatus status;
    private String note;
    private int remindMeBeforeDays;
    private boolean reminded;
}
