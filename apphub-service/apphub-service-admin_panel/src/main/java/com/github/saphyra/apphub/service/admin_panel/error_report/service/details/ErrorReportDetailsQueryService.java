package com.github.saphyra.apphub.service.admin_panel.error_report.service.details;

import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReport;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReportDao;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReportDto;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReportStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ErrorReportDetailsQueryService {
    private final ErrorReportDao errorReportDao;
    private final ErrorReportToResponseConverter converter;

    public ErrorReport findById(UUID id) {
        Optional<ErrorReportDto> errorReportOptional = errorReportDao.findById(id);

        errorReportOptional.filter(errorReport -> errorReport.getStatus() == ErrorReportStatus.UNREAD)
            .ifPresent(errorReport -> {
                errorReport.setStatus(ErrorReportStatus.READ);
                errorReportDao.save(errorReport);
            });

        return errorReportOptional
            .map(converter::convert)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "ErrorReport not found with id " + id));
    }
}
