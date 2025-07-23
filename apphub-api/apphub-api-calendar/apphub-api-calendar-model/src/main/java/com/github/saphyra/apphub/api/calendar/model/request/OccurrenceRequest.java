package com.github.saphyra.apphub.api.calendar.model.request;

import com.github.saphyra.apphub.api.calendar.model.OccurrenceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OccurrenceRequest {
    private LocalDate date;
    private LocalTime time;
    private OccurrenceStatus status;
    private String note;
    private Integer remindMeBeforeDays;
}
