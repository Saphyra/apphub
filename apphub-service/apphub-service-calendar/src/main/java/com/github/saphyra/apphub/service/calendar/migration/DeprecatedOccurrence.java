package com.github.saphyra.apphub.service.calendar.migration;

import com.github.saphyra.apphub.api.calendar.model.OccurrenceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@AllArgsConstructor
@Builder
@Data
public class DeprecatedOccurrence {
    private final UUID occurrenceId;
    private final UUID eventId;
    private final UUID userId;
    private final LocalDate date;
    private final LocalTime time;
    private OccurrenceStatus status;
    private String note;
}
