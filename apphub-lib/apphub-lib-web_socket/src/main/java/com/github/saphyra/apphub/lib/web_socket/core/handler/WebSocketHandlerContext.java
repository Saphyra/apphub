package com.github.saphyra.apphub.lib.web_socket.core.handler;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import lombok.Builder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class WebSocketHandlerContext {
    private final ObjectMapperWrapper objectMapperWrapper;
    private final UuidConverter uuidConverter;
    private final DateTimeUtil dateTimeUtil;
    private final ErrorReporterService errorReporterService;

    private final int webSocketSessionExpirationSeconds;

    @Builder
    WebSocketHandlerContext(
        ObjectMapperWrapper objectMapperWrapper,
        UuidConverter uuidConverter,
        DateTimeUtil dateTimeUtil,
        ErrorReporterService errorReporterService,
        @Value("${webSocketSession.expirationSeconds}") int webSocketSessionExpirationSeconds
    ) {
        this.objectMapperWrapper = objectMapperWrapper;
        this.uuidConverter = uuidConverter;
        this.dateTimeUtil = dateTimeUtil;
        this.errorReporterService = errorReporterService;
        this.webSocketSessionExpirationSeconds = webSocketSessionExpirationSeconds;
    }
}
