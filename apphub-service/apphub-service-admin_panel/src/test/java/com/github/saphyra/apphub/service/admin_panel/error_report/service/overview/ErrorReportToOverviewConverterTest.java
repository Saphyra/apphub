package com.github.saphyra.apphub.service.admin_panel.error_report.service.overview;

import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReportOverview;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReport;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReportStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ErrorReportToOverviewConverterTest {
    private static final UUID ID = UUID.randomUUID();
    private static final LocalDateTime CREATED_AT = LocalDateTime.now();
    private static final Integer RESPONSE_STATUS = 24;
    private static final String MESSAGE = "message";
    private static final String SERVICE = "service";

    @InjectMocks
    private ErrorReportToOverviewConverter underTest;

    @Test
    public void convert() {
        ErrorReport errorReport = ErrorReport.builder()
            .id(ID)
            .createdAt(CREATED_AT)
            .responseStatus(RESPONSE_STATUS)
            .message(MESSAGE)
            .status(ErrorReportStatus.UNREAD)
            .service(SERVICE)
            .build();

        ErrorReportOverview result = underTest.convert(errorReport);

        assertThat(result.getId()).isEqualTo(ID);
        assertThat(result.getCreatedAt()).isEqualTo(CREATED_AT.toString());
        assertThat(result.getMessage()).isEqualTo(MESSAGE);
        assertThat(result.getService()).isEqualTo(SERVICE);
        assertThat(result.getStatus()).isEqualTo(ErrorReportStatus.UNREAD.name());
    }
}