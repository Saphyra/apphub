package com.github.saphyra.apphub.service.admin_panel.error_report.service.report;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import org.springframework.stereotype.Component;

import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReportModel;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReport;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReportDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class ReportErrorService {
    private final ErrorReportFactory errorReportFactory;
    private final ErrorReportDao errorReportDao;

    public void saveReport(ErrorReportModel model) {
        if (!isNull(model.getId())) {
            throw ExceptionFactory.invalidParam("id", "must be null");
        }

        if (isBlank(model.getMessage())) {
            throw ExceptionFactory.invalidParam("message", "must not be null");
        }

        ErrorReport errorReport = errorReportFactory.create(model);

        errorReportDao.save(errorReport);
        log.info("ErrorReport created with id {} for message {}", errorReport.getId(), errorReport.getMessage());
    }
}
