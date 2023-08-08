package com.github.saphyra.apphub.integration.structure.api.admin_panel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MarkUserForDeletionRequest {
    private LocalDate date;
    private String time;
    private String password;
}
