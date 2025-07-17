package com.github.saphyra.apphub.service.calendar_deprecated.dao.occurance;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
public class Occurrence {
    private final UUID occurrenceId;
    private final UUID eventId;
    private final UUID userId;
    private final LocalDate date;
    private final LocalTime time;
    private OccurrenceStatus status;
    private String note;
    private final OccurrenceType type;
}
