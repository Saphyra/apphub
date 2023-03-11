package com.github.saphyra.apphub.api.calendar.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventSearchResponse {
    private UUID eventId;
    private LocalDateTime time;
    private RepetitionType repetitionType;
    private String repetitionData;
    private String title;
    private String content;
    private List<OccurrenceSearchResponse> occurrences;
}
