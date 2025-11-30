package com.github.saphyra.apphub.service.admin_panel.error_report.repository;

import com.github.saphyra.apphub.api.admin_panel.model.model.ExceptionModel;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ErrorReportConverterTest {
    private static final String ID_STRING = "id";
    private static final LocalDateTime CREATED_AT = LocalDateTime.now();
    private static final String MESSAGE = "message";
    private static final Integer RESPONSE_STATUS = 325;
    private static final String RESPONSE_BODY = "response-body";
    private static final String EXCEPTION_STRING = "exception";
    private static final UUID ID = UUID.randomUUID();
    private static final String SERVICE = "service";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ErrorReportConverter underTest;

    @Mock
    private ExceptionModel exceptionModel;

    @Test
    public void convertEntity() {
        given(uuidConverter.convertEntity(ID_STRING)).willReturn(ID);
        given(objectMapper.readValue(EXCEPTION_STRING, ExceptionModel.class)).willReturn(exceptionModel);

        ErrorReportEntity entity = ErrorReportEntity.builder()
            .id(ID_STRING)
            .createdAt(CREATED_AT)
            .message(MESSAGE)
            .responseStatus(RESPONSE_STATUS)
            .responseBody(RESPONSE_BODY)
            .exception(EXCEPTION_STRING)
            .status(ErrorReportStatus.UNREAD.name())
            .service(SERVICE)
            .build();

        ErrorReportDto result = underTest.convertEntity(entity);

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
    public void convertDomain() {
        given(uuidConverter.convertDomain(ID)).willReturn(ID_STRING);
        given(objectMapper.writeValueAsString(exceptionModel)).willReturn(EXCEPTION_STRING);

        ErrorReportDto domain = ErrorReportDto.builder()
            .id(ID)
            .createdAt(CREATED_AT)
            .message(MESSAGE)
            .responseStatus(RESPONSE_STATUS)
            .responseBody(RESPONSE_BODY)
            .exception(exceptionModel)
            .status(ErrorReportStatus.UNREAD)
            .service(SERVICE)
            .build();

        ErrorReportEntity result = underTest.convertDomain(domain);

        assertThat(result.getId()).isEqualTo(ID_STRING);
        assertThat(result.getCreatedAt()).isEqualTo(CREATED_AT);
        assertThat(result.getMessage()).isEqualTo(MESSAGE);
        assertThat(result.getResponseStatus()).isEqualTo(RESPONSE_STATUS);
        assertThat(result.getResponseBody()).isEqualTo(RESPONSE_BODY);
        assertThat(result.getException()).isEqualTo(EXCEPTION_STRING);
        assertThat(result.getService()).isEqualTo(SERVICE);
        assertThat(result.getStatus()).isEqualTo(ErrorReportStatus.UNREAD.name());
    }
}