package com.github.saphyra.apphub.api.user.model.request;

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
