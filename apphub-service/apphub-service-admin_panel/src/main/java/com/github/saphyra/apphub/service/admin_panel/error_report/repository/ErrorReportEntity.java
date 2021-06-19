package com.github.saphyra.apphub.service.admin_panel.error_report.repository;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(schema = "admin_panel", name = "error_report")
 class ErrorReportEntity {
    @Id
    private String id;
    private LocalDateTime createdAt;
    private String message;
    private Integer responseStatus;
    private String responseBody;
    private String exception;
}
