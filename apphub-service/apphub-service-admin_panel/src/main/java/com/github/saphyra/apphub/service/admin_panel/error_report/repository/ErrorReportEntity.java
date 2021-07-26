package com.github.saphyra.apphub.service.admin_panel.error_report.repository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(schema = "admin_panel", name = "error_report")
public class ErrorReportEntity {
    @Id
    private String id;
    private LocalDateTime createdAt;
    private String message;
    private Integer responseStatus;
    private String responseBody;
    private String exception;
    private String status;
}
