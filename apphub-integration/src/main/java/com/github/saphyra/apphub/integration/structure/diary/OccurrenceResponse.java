package com.github.saphyra.apphub.integration.structure.diary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OccurrenceResponse {
    private UUID occurrenceId;
    private UUID eventId;
    private String status;
    private String title;
    private String content;
    private String note;
}
