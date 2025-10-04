package com.github.saphyra.apphub.service.calendar.domain.occurrence.dao;

import com.github.saphyra.apphub.api.calendar.model.OccurrenceStatus;
import jakarta.annotation.Nullable;
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
@Builder(toBuilder = true)
@EqualsAndHashCode(exclude = "occurrenceId")
public class Occurrence {
    private final UUID occurrenceId;
    private final UUID userId;
    private final UUID eventId;
    private LocalDate date;
    @Nullable
    private LocalTime time;
    private OccurrenceStatus status;
    private String note;
    @Nullable
    private Integer remindMeBeforeDays;
    private Boolean reminded;
}
