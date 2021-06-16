package com.github.saphyra.apphub.service.admin_panel.error_report;

import org.springframework.web.bind.annotation.RestController;

import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReportModel;
import com.github.saphyra.apphub.api.admin_panel.server.ErrorReporterController;
import com.github.saphyra.apphub.service.admin_panel.error_report.service.report.ReportErrorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
//TODO unit test
//TODO int test
public class ErrorReporterControllerImpl implements ErrorReporterController {
    private final ReportErrorService reportErrorService;

    @Override
    public void reportError(ErrorReportModel model) {
        log.info("Creating errorReport for message {}", model.getMessage());
        reportErrorService.saveReport(model);
    }
}
