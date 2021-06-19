package com.github.saphyra.apphub.api.admin_panel.model.model;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ErrorReportModel {
    private UUID id;
    private LocalDateTime createdAt;
    private String message;
    private Integer responseStatus;
    private String responseBody;
    private String exception;
}
