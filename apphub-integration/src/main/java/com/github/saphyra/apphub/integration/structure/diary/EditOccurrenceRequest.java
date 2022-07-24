package com.github.saphyra.apphub.integration.structure.diary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class EditOccurrenceRequest {
    private ReferenceDate referenceDate;
    private String title;
    private String content;
    private String note;
}
