package com.github.saphyra.apphub.service.calendar.dao.event;

import com.github.saphyra.apphub.api.calendar.model.RepetitionType;
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
public class Event {
    private final UUID eventId;
    private final UUID userId;
    private final LocalDate startDate;
    private final LocalTime time;
    private RepetitionType repetitionType;
    private String repetitionData;
    private String title;
    private String content;
    private int repeat;
}
