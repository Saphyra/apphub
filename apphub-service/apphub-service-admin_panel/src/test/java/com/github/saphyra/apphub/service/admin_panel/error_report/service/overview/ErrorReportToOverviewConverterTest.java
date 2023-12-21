package com.github.saphyra.apphub.service.admin_panel.error_report.service.overview;

import com.github.saphyra.apphub.api.admin_panel.model.model.ErrorReportOverview;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReportDto;
import com.github.saphyra.apphub.service.admin_panel.error_report.repository.ErrorReportStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ErrorReportToOverviewConverterTest {
    private static final UUID ID = UUID.randomUUID();
    private static final LocalDateTime CREATED_AT = LocalDateTime.now();
    private static final Integer RESPONSE_STATUS = 24;
    private static final String MESSAGE = "message";
    private static final String SERVICE = "service";
    private static final String FORMATTED_CREATED_AT = "formatted-created-at";

    @Mock
    private DateTimeUtil dateTimeUtil;

    @InjectMocks
    private ErrorReportToOverviewConverter underTest;

    @Test
    public void convert() {
        ErrorReportDto errorReport = ErrorReportDto.builder()
            .id(ID)
            .createdAt(CREATED_AT)
            .responseStatus(RESPONSE_STATUS)
            .message(MESSAGE)
            .status(ErrorReportStatus.UNREAD)
            .service(SERVICE)
            .build();
        given(dateTimeUtil.format(CREATED_AT)).willReturn(FORMATTED_CREATED_AT);

        ErrorReportOverview result = underTest.convert(errorReport);

        assertThat(result.getId()).isEqualTo(ID);
        assertThat(result.getCreatedAt()).isEqualTo(FORMATTED_CREATED_AT);
        assertThat(result.getMessage()).isEqualTo(MESSAGE);
        assertThat(result.getService()).isEqualTo(SERVICE);
        assertThat(result.getStatus()).isEqualTo(ErrorReportStatus.UNREAD.name());
    }
}