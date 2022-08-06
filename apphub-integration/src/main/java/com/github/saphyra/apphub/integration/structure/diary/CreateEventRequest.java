package com.github.saphyra.apphub.integration.structure.diary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CreateEventRequest {
    private ReferenceDate referenceDate;
    private LocalDate date;
    private String title;
    private String content;
    private RepetitionType repetitionType;
    private Integer repetitionDays;
    private List<DayOfWeek> repetitionDaysOfWeek;
    private List<Integer> repetitionDaysOfMonth;
}
