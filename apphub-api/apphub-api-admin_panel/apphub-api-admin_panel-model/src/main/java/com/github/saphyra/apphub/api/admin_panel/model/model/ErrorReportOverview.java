package com.github.saphyra.apphub.api.admin_panel.model.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorReportOverview {
    private UUID id;
    private String createdAt;
    private Integer responseStatus;
    private String message;
    private String status;
    private String service;
}
