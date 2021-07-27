package com.github.saphyra.apphub.service.admin_panel.error_report.repository;

import com.github.saphyra.apphub.lib.exception.ExceptionFactory;

import java.util.Arrays;

public enum ErrorReportStatus {
    UNREAD, READ, MARKED;

    public static ErrorReportStatus parse(String in) {
        return Arrays.stream(values())
            .filter(errorReportStatus -> errorReportStatus.name().equalsIgnoreCase(in))
            .findFirst()
            .orElseThrow(() -> ExceptionFactory.invalidParam("status", "unknown value"));
    }
}
