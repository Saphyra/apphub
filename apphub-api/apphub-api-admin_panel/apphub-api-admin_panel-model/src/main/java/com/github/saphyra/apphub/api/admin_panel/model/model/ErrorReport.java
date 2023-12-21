package com.github.saphyra.apphub.api.admin_panel.model.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ErrorReport {
    private UUID id;
    private LocalDateTime createdAt;
    private String message;
    private String service;
    private Integer responseStatus;
    private String responseBody;
    private ExceptionModel exception;
    private String status;
}
