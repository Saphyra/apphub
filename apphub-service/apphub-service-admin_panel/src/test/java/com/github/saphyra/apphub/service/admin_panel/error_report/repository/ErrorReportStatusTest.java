package com.github.saphyra.apphub.service.admin_panel.error_report.repository;


import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class ErrorReportStatusTest {
    @Test
    public void parse() {
        assertThat(ErrorReportStatus.parse(ErrorReportStatus.MARKED.name())).isEqualTo(ErrorReportStatus.MARKED);
    }

    @Test
    public void parse_failed() {
        Throwable ex = catchThrowable(() -> ErrorReportStatus.parse("asd"));

        ExceptionValidator.validateInvalidParam(ex, "status", "unknown value");
    }
}