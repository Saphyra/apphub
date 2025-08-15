package com.github.saphyra.apphub.api.calendar.model.response;

import com.github.saphyra.apphub.api.calendar.model.RepetitionType;
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
public class EventResponse {
    private UUID eventId;
    private RepetitionType repetitionType;
    private Object repetitionData;
    private Integer repeatForDays;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime time;
    private String title;
    private String content;
    private Integer remindMeBeforeDays;
}
