package com.github.saphyra.apphub.api.diary.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class EditOccurrenceRequest {
    private LocalDate date;
    private String title;
    private String content;
    private String note;
}
