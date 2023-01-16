package com.github.saphyra.apphub.service.admin_panel.error_report.service;

import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReportDao;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReportStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class MarkErrorReportService {
    private final ErrorReportDao errorReportDao;

    @Transactional
    public void mark(List<UUID> ids, String statusString) {
        ErrorReportStatus status = ErrorReportStatus.parse(statusString);

        errorReportDao.findAllById(ids)
            .forEach(errorReport -> {
                errorReport.setStatus(status);
                errorReportDao.save(errorReport);
            });
    }
}
