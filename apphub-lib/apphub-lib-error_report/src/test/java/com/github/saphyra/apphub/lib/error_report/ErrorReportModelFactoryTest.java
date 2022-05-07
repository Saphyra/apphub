package com.github.saphyra.apphub.lib.error_report;

import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReportModel;
import com.github.saphyra.apphub.api.admin_panel.model.model.ExceptionModel;
import com.github.saphyra.apphub.lib.common_domain.ErrorResponse;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
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
    private static final String MESSAGE = "message";
    private static final String THREAD = "thread";
    private static final String TYPE = "type";
    private static final String APPLICATION_NAME = "application-name";

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @Mock
    private ExceptionMapper exceptionMapper;

    @Mock
    private CommonConfigProperties commonConfigProperties;

    @InjectMocks
    private ErrorReportModelFactory underTest;

    @Mock
    private ErrorResponse errorResponse;

    @Mock
    private RuntimeException exception;

    @Mock
    private ExceptionModel exceptionModel;

    @Test
    public void create() {
        given(dateTimeUtil.getCurrentTime()).willReturn(CURRENT_DATE);
        given(objectMapperWrapper.writeValueAsString(errorResponse)).willReturn(ERROR_RESPONSE);
        given(exceptionMapper.map(exception)).willReturn(exceptionModel);
        given(exception.getMessage()).willReturn(MESSAGE);
        given(exceptionModel.getThread()).willReturn(THREAD);
        given(exceptionModel.getType()).willReturn(TYPE);
        given(commonConfigProperties.getApplicationName()).willReturn(APPLICATION_NAME);

        ErrorReportModel result = underTest.create(HttpStatus.NOT_FOUND, errorResponse, exception);

        assertThat(result.getId()).isNull();
        assertThat(result.getCreatedAt()).isEqualTo(CURRENT_DATE);
        assertThat(result.getMessage()).isEqualTo(String.format("%s on thread %s: %s", TYPE, THREAD, MESSAGE));
        assertThat(result.getResponseStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(result.getResponseBody()).isEqualTo(ERROR_RESPONSE);
        assertThat(result.getException()).isEqualTo(exceptionModel);
        assertThat(result.getService()).isEqualTo(APPLICATION_NAME);
    }

    @Test
    public void createForMessage() {
        given(dateTimeUtil.getCurrentTime()).willReturn(CURRENT_DATE);
        given(exceptionMapper.map(exception)).willReturn(exceptionModel);
        given(commonConfigProperties.getApplicationName()).willReturn(APPLICATION_NAME);

        ErrorReportModel result = underTest.create(MESSAGE, exception);

        assertThat(result.getId()).isNull();
        assertThat(result.getCreatedAt()).isEqualTo(CURRENT_DATE);
        assertThat(result.getMessage()).isEqualTo(MESSAGE);
        assertThat(result.getResponseStatus()).isNull();
        assertThat(result.getResponseBody()).isNull();
        assertThat(result.getException()).isEqualTo(exceptionModel);
        assertThat(result.getService()).isEqualTo(APPLICATION_NAME);
    }
}