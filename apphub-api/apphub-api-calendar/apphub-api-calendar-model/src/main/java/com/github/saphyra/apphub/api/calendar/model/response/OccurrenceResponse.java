package com.github.saphyra.apphub.api.calendar.model.response;

import com.github.saphyra.apphub.api.calendar.model.OccurrenceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class OccurrenceResponse {
    private UUID occurrenceId;
    private UUID eventId;
    private LocalDate date;
    private LocalTime time;
    private OccurrenceStatus status;
    private String note;
    private Integer remindMeBeforeDays;
    private Boolean reminded;
}
