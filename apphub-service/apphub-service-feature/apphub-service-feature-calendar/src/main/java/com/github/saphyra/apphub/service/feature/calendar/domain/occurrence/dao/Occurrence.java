package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao;

import com.github.saphyra.apphub.api.feature.calendar.model.OccurrenceStatus;
import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder(toBuilder = true)
public class Occurrence {
    @NonNull
    private final UUID userId;
    @NonNull
    private UUID eventId;
    @NonNull
    private final UUID occurrenceId;
    @NonNull
    private LocalDate date;
    @Nullable
    private LocalTime time;
    @NonNull
    private OccurrenceStatus status;
    @NonNull
    private String note;
    @Nullable //If null, Event's value is used
    private Integer remindMeBeforeDays;
    private boolean reminded;
    @Nullable //If null Event's value is used
    private Boolean autoDone;
}
