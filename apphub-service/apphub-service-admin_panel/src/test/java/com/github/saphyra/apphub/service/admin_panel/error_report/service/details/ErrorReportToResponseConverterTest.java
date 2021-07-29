package com.github.saphyra.apphub.service.admin_panel.error_report.service.details;

import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReportModel;
import com.github.saphyra.apphub.api.admin_panel.model.model.ExceptionModel;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReport;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReportStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ErrorReportToResponseConverterTest {
    private static final UUID ID = UUID.randomUUID();
    private static final LocalDateTime CREATED_AT = LocalDateTime.now();
    private static final String MESSAGE = "message";
    private static final Integer RESPONSE_STATUS = 42;
    private static final String RESPONSE_BODY = "response-body";

    @InjectMocks
    private ErrorReportToResponseConverter underTest;

    @Mock
    private ExceptionModel exceptionModel;

    @Test
    public void convert() {
        ErrorReport errorReport = ErrorReport.builder()
            .id(ID)
            .createdAt(CREATED_AT)
            .message(MESSAGE)
            .responseStatus(RESPONSE_STATUS)
            .responseBody(RESPONSE_BODY)
            .exception(exceptionModel)
            .status(ErrorReportStatus.READ)
            .build();

        ErrorReportModel result = underTest.convert(errorReport);

        assertThat(result.getId()).isEqualTo(ID);
        assertThat(result.getCreatedAt()).isEqualTo(CREATED_AT);
        assertThat(result.getMessage()).isEqualTo(MESSAGE);
        assertThat(result.getResponseStatus()).isEqualTo(RESPONSE_STATUS);
        assertThat(result.getResponseBody()).isEqualTo(RESPONSE_BODY);
        assertThat(result.getException()).isEqualTo(exceptionModel);
        assertThat(result.getStatus()).isEqualTo(ErrorReportStatus.READ.name());
    }
}