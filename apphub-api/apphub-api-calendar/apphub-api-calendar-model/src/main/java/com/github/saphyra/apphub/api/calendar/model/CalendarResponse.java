package com.github.saphyra.apphub.api.calendar.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarResponse {
    private LocalDate date;
    private List<OccurrenceResponse> events;
}
