package com.github.saphyra.apphub.integration.structure.api.calendar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ReferenceDate {
    private LocalDate month;
    private LocalDate day;
}
