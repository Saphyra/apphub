package com.github.saphyra.apphub.service.admin_panel.error_report.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import jakarta.transaction.Transactional;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface ErrorReportRepository extends CrudRepository<ErrorReportEntity, String>, JpaSpecificationExecutor<ErrorReportEntity> {
    @Transactional
    void deleteByStatus(String status);

    @Modifying
    @Query("DELETE ErrorReportEntity e WHERE e.status not in :statuses")
    void deleteByStatusNotIn(@Param("statuses") List<String> statuses);
}
