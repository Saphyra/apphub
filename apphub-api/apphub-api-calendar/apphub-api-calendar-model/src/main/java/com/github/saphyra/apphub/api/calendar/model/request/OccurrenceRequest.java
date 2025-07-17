package com.github.saphyra.apphub.api.calendar.model.request;

import com.github.saphyra.apphub.api.calendar.model.OccurrenceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OccurrenceRequest {
    private UUID eventId;
    private LocalDate date;
    private LocalTime time;
    private OccurrenceStatus status;
    private String note;
    private LocalDate remindAt;
}
