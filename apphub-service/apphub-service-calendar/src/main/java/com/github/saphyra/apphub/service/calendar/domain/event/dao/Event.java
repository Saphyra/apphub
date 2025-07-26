package com.github.saphyra.apphub.service.calendar.domain.event.dao;

import com.github.saphyra.apphub.api.calendar.model.RepetitionType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Data
public class Event {
    private final UUID eventId;
    private final UUID userId;
    private RepetitionType repetitionType;
    private String repetitionData;
    private Integer repeatForDays;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime time;
    private String title;
    private String content;
    private Integer remindMeBeforeDays;
}
