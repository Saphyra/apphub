package com.github.saphyra.apphub.service.admin_panel.error_report.repository;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorReport {
    private UUID id;
    private LocalDateTime createdAt;
    private String message;
    private Integer responseStatus;
    private String responseBody;
    private String exception;
}
