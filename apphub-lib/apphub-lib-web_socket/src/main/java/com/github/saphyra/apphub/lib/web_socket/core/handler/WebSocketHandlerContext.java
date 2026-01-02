package com.github.saphyra.apphub.lib.web_socket.core.handler;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import lombok.Builder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@Getter
public class WebSocketHandlerContext {
    private final ObjectMapper objectMapper;
    private final UuidConverter uuidConverter;
    private final DateTimeUtil dateTimeUtil;
    private final ErrorReporterService errorReporterService;

    private final int webSocketSessionExpirationSeconds;

    @Builder
    WebSocketHandlerContext(
        ObjectMapper objectMapper,
        UuidConverter uuidConverter,
        DateTimeUtil dateTimeUtil,
        ErrorReporterService errorReporterService,
        @Value("${webSocketSession.expirationSeconds}") int webSocketSessionExpirationSeconds
    ) {
        this.objectMapper = objectMapper;
        this.uuidConverter = uuidConverter;
        this.dateTimeUtil = dateTimeUtil;
        this.errorReporterService = errorReporterService;
        this.webSocketSessionExpirationSeconds = webSocketSessionExpirationSeconds;
    }
}
