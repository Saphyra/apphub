package com.github.saphyra.apphub.service.admin_panel.error_report.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import jakarta.transaction.Transactional;

import java.util.List;

interface ErrorReportRepository extends CrudRepository<ErrorReportEntity, String>, JpaSpecificationExecutor<ErrorReportEntity> {
    @Transactional
    void deleteByStatus(String status);

    void deleteByStatusNotIn(List<String> statuses);
}
