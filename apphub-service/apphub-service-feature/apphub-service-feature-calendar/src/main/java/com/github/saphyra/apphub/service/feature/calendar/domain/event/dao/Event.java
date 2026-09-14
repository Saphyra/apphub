package com.github.saphyra.apphub.service.feature.calendar.domain.event.dao;

import com.github.saphyra.apphub.api.feature.calendar.model.RepetitionType;
import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
@EqualsAndHashCode(exclude = "masked")
public class Event {
    @NonNull
    private final UUID eventId;
    @NonNull
    private final UUID userId;
    @NonNull
    private RepetitionType repetitionType;
    @Nullable
    private String repetitionData;
    @NonNull
    private Integer repeatForDays;
    @NonNull
    private LocalDate startDate;
    @Nullable //One-time event has no end date
    private LocalDate endDate;
    @Nullable
    private LocalTime time;
    @NonNull
    private String title;
    @NonNull
    private String content;
    private int remindMeBeforeDays;
    private boolean expirationNotified;
    private boolean archived;
    private boolean autoDone;

    private boolean masked;

    public Event setMasked(boolean masked) {
        this.masked = masked;

        return this;
    }
}
