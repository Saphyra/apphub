package com.github.saphyra.apphub.lib.error_handler.service.error_report;

import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReportModel;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ErrorReportModelFactoryTest {
    private static final LocalDateTime CURRENT_DATE = LocalDateTime.now();
    private static final String ERROR_RESPONSE = "error-response";
    private static final String EXCEPTION = "exception";
    private static final String MESSAGE = "message";

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @InjectMocks
    private ErrorReportModelFactory underTest;

    @Mock
    private ErrorResponse errorResponse;

    @Mock
    private RuntimeException exception;

    @Test
    public void create() {
        given(dateTimeUtil.getCurrentDate()).willReturn(CURRENT_DATE);
        given(objectMapperWrapper.writeValueAsString(errorResponse)).willReturn(ERROR_RESPONSE);
        given(objectMapperWrapper.writeValueAsString(exception)).willReturn(EXCEPTION);
        given(exception.getMessage()).willReturn(MESSAGE);

        ErrorReportModel result = underTest.create(HttpStatus.NOT_FOUND, errorResponse, exception);

        assertThat(result.getId()).isNull();
        assertThat(result.getCreatedAt()).isEqualTo(CURRENT_DATE);
        assertThat(result.getMessage()).isEqualTo(MESSAGE);
        assertThat(result.getResponseStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(result.getResponseBody()).isEqualTo(ERROR_RESPONSE);
        assertThat(result.getException()).isEqualTo(EXCEPTION);
    }

    @Test
    public void createForMessage() {
        given(dateTimeUtil.getCurrentDate()).willReturn(CURRENT_DATE);

        ErrorReportModel result = underTest.create(MESSAGE);

        assertThat(result.getId()).isNull();
        assertThat(result.getCreatedAt()).isEqualTo(CURRENT_DATE);
        assertThat(result.getMessage()).isEqualTo(MESSAGE);
        assertThat(result.getResponseStatus()).isNull();
        assertThat(result.getResponseBody()).isNull();
        assertThat(result.getException()).isNull();
    }
}