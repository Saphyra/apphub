package com.github.saphyra.apphub.integration.structure.diary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Occurrence {
    private UUID occurrenceId;
    private UUID eventId;
    private UUID userId;
    private String date;
    private String status;
    private String note;
}
