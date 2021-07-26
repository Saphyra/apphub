package com.github.saphyra.apphub.service.admin_panel.error_report.service.overview;

import com.github.saphyra.apphub.api.admin_panel.model.model.GetErrorReportsRequest;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReportStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ErrorReportOverviewSpecificationFactoryTest {
    private static final String MESSAGE = "message";
    private static final Integer STATUS_CODE = 467;
    private static final String START_TIME_STRING = "start-time";
    private static final String END_TIME_STRING = "end-time";
    private static final LocalDateTime START_TIME = LocalDateTime.now();
    private static final LocalDateTime END_TIME = LocalDateTime.now();

    @Mock
    private CustomLocalDateTimeParser localDateTimeParser;

    @InjectMocks
    private ErrorReportOverviewSpecificationFactory underTest;

    @Test
    public void create() {
        GetErrorReportsRequest request = GetErrorReportsRequest.builder()
            .message(MESSAGE)
            .statusCode(STATUS_CODE)
            .startTime(START_TIME_STRING)
            .endTime(END_TIME_STRING)
            .status(ErrorReportStatus.UNREAD.name())
            .build();

        given(localDateTimeParser.parse(START_TIME_STRING)).willReturn(START_TIME);
        given(localDateTimeParser.parse(END_TIME_STRING)).willReturn(END_TIME);

        ErrorReportOverviewSpecification result = underTest.create(request);

        assertThat(result.getMessage()).isEqualTo(MESSAGE);
        assertThat(result.getStatusCode()).isEqualTo(STATUS_CODE);
        assertThat(result.getStartTime()).isEqualTo(START_TIME);
        assertThat(result.getEndTime()).isEqualTo(END_TIME);
        assertThat(result.getStatus()).isEqualTo(ErrorReportStatus.UNREAD);
    }

    @Test
    public void create_nullValues() {
        GetErrorReportsRequest request = GetErrorReportsRequest.builder()
            .message(null)
            .statusCode(null)
            .startTime(null)
            .endTime(" ")
            .status(null)
            .build();

        ErrorReportOverviewSpecification result = underTest.create(request);

        assertThat(result.getMessage()).isNull();
        assertThat(result.getStatusCode()).isNull();
        assertThat(result.getStartTime()).isNull();
        assertThat(result.getEndTime()).isNull();
        assertThat(result.getStatus()).isNull();
    }
}