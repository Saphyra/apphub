package com.github.saphyra.apphub.service.admin_panel.error_report.repository;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
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
public class ErrorReportConverterTest {
    private static final String ID_STRING = "id";
    private static final LocalDateTime CREATED_AT = LocalDateTime.now();
    private static final String MESSAGE = "message";
    private static final Integer RESPONSE_STATUS = 325;
    private static final String RESPONSE_BODY = "response-body";
    private static final String EXCEPTION = "exception";
    private static final UUID ID = UUID.randomUUID();

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private ErrorReportConverter underTest;

    @Test
    public void convertEntity() {
        given(uuidConverter.convertEntity(ID_STRING)).willReturn(ID);

        ErrorReportEntity entity = ErrorReportEntity.builder()
            .id(ID_STRING)
            .createdAt(CREATED_AT)
            .message(MESSAGE)
            .responseStatus(RESPONSE_STATUS)
            .responseBody(RESPONSE_BODY)
            .exception(EXCEPTION)
            .build();

        ErrorReport result = underTest.convertEntity(entity);

        assertThat(result.getId()).isEqualTo(ID);
        assertThat(result.getCreatedAt()).isEqualTo(CREATED_AT);
        assertThat(result.getMessage()).isEqualTo(MESSAGE);
        assertThat(result.getResponseStatus()).isEqualTo(RESPONSE_STATUS);
        assertThat(result.getResponseBody()).isEqualTo(RESPONSE_BODY);
        assertThat(result.getException()).isEqualTo(EXCEPTION);
    }

    @Test
    public void convertDomain() {
        given(uuidConverter.convertDomain(ID)).willReturn(ID_STRING);

        ErrorReport domain = ErrorReport.builder()
            .id(ID)
            .createdAt(CREATED_AT)
            .message(MESSAGE)
            .responseStatus(RESPONSE_STATUS)
            .responseBody(RESPONSE_BODY)
            .exception(EXCEPTION)
            .build();

        ErrorReportEntity result = underTest.convertDomain(domain);

        assertThat(result.getId()).isEqualTo(ID_STRING);
        assertThat(result.getCreatedAt()).isEqualTo(CREATED_AT);
        assertThat(result.getMessage()).isEqualTo(MESSAGE);
        assertThat(result.getResponseStatus()).isEqualTo(RESPONSE_STATUS);
        assertThat(result.getResponseBody()).isEqualTo(RESPONSE_BODY);
        assertThat(result.getException()).isEqualTo(EXCEPTION);
    }
}