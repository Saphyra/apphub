package com.github.saphyra.apphub.service.admin_panel.error_report.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

interface ErrorReportRepository extends CrudRepository<ErrorReportEntity, String>, JpaSpecificationExecutor<ErrorReportEntity> {
}
