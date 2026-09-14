package com.github.saphyra.apphub.api.feature.calendar.model.request;

import com.github.saphyra.apphub.api.feature.calendar.model.RepetitionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class EventRequest {
    private RepetitionType repetitionType;
    private Object repetitionData;
    private Integer repeatForDays;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime time;
    private String title;
    private String content;
    private Integer remindMeBeforeDays;
    private Map<UUID, UUID> labels; //Map<labelId, userId>
    private Boolean archived;
    private Boolean autoDone;
}
