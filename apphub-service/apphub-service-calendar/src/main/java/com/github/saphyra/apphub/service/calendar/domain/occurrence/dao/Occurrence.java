package com.github.saphyra.apphub.service.calendar.domain.occurrence.dao;

import com.github.saphyra.apphub.api.calendar.model.OccurrenceStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
@EqualsAndHashCode(exclude = "occurrenceId")
public class Occurrence {
    private final UUID occurrenceId;
    private final UUID userId;
    private final UUID eventId;
    private LocalDate date;
    private LocalTime time;
    private OccurrenceStatus status;
    private String note;
    private Integer remindMeBeforeDays;
    private Boolean reminded;
}
