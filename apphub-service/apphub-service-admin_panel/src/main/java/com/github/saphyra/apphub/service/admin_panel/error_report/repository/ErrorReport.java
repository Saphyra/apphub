package com.github.saphyra.apphub.service.admin_panel.error_report.repository;

import com.github.saphyra.apphub.api.admin_panel.model.model.ExceptionModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

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
    private String service;
    private ExceptionModel exception;
    private ErrorReportStatus status;
}
