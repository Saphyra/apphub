package com.github.saphyra.apphub.service.admin_panel.error_report.repository;

import org.springframework.stereotype.Component;

import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ErrorReportDao extends AbstractDao<ErrorReportEntity, ErrorReport, String, ErrorReportRepository> {
    public ErrorReportDao(ErrorReportConverter converter, ErrorReportRepository repository) {
        super(converter, repository);
    }
}
