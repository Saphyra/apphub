package com.github.saphyra.apphub.service.admin_panel.error_report.service.report;

import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReportModel;
import com.github.saphyra.apphub.api.admin_panel.model.model.ExceptionModel;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
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
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ErrorReportFactoryTest {
    private static final UUID ID = UUID.randomUUID();
    private static final LocalDateTime CREATED_AT = LocalDateTime.now();
    private static final String MESSAGE = "message";
    private static final Integer RESPONSE_STATUS = 24;
    private static final String RESPONSE_BODY = "response-body";
    private static final String SERVICE = "service";

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @InjectMocks
    private ErrorReportFactory underTest;

    @Mock
    private ExceptionModel exceptionModel;

    @Test
    public void create() {
        ErrorReportModel model = ErrorReportModel.builder()
            .id(ID)
            .createdAt(CREATED_AT)
            .message(MESSAGE)
            .responseStatus(RESPONSE_STATUS)
            .responseBody(RESPONSE_BODY)
            .exception(exceptionModel)
            .service(SERVICE)
            .build();

        ErrorReport result = underTest.create(model);

        assertThat(result.getId()).isEqualTo(ID);
        assertThat(result.getCreatedAt()).isEqualTo(CREATED_AT);
        assertThat(result.getMessage()).isEqualTo(MESSAGE);
        assertThat(result.getResponseStatus()).isEqualTo(RESPONSE_STATUS);
        assertThat(result.getResponseBody()).isEqualTo(RESPONSE_BODY);
        assertThat(result.getException()).isEqualTo(exceptionModel);
        assertThat(result.getService()).isEqualTo(SERVICE);
        assertThat(result.getStatus()).isEqualTo(ErrorReportStatus.UNREAD);
    }

    @Test
    public void create_generateForMissing() {
        ErrorReportModel model = ErrorReportModel.builder()
            .message(MESSAGE)
            .responseStatus(RESPONSE_STATUS)
            .responseBody(RESPONSE_BODY)
            .exception(exceptionModel)
            .service(SERVICE)
            .build();

        given(idGenerator.randomUuid()).willReturn(ID);
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CREATED_AT);

        ErrorReport result = underTest.create(model);

        assertThat(result.getId()).isEqualTo(ID);
        assertThat(result.getCreatedAt()).isEqualTo(CREATED_AT);
        assertThat(result.getMessage()).isEqualTo(MESSAGE);
        assertThat(result.getResponseStatus()).isEqualTo(RESPONSE_STATUS);
        assertThat(result.getResponseBody()).isEqualTo(RESPONSE_BODY);
        assertThat(result.getException()).isEqualTo(exceptionModel);
        assertThat(result.getService()).isEqualTo(SERVICE);
        assertThat(result.getStatus()).isEqualTo(ErrorReportStatus.UNREAD);
    }
}