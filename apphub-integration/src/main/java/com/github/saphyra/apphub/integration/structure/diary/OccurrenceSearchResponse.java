package com.github.saphyra.apphub.integration.structure.diary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OccurrenceSearchResponse {
    private UUID occurrenceId;
    private LocalDate date;
    private LocalTime time;
    private String status;
    private String note;
}
