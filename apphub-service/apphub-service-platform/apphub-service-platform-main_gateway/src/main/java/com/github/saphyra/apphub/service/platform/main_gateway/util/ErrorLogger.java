package com.github.saphyra.apphub.service.platform.main_gateway.util;

import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.exception.LoggedException;
import com.github.saphyra.apphub.lib.exception.NotLoggedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ErrorLogger {
    private final ErrorReporterService errorReporterService;

    public void log(Throwable throwable) {
        if (throwable instanceof LoggedException) {
            log.error("Exception occurred", throwable);
        } else if (throwable instanceof NotLoggedException) {
            log.warn("Exception occurred: {}", throwable.getMessage());
        } else {
            errorReporterService.report("Exception occurred", throwable);
        }
    }
}
