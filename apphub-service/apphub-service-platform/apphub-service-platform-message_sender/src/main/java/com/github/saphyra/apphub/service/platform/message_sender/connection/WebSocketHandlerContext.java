package com.github.saphyra.apphub.service.platform.message_sender.connection;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
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

    private final int webSocketSessionExpirationSeconds;

    @Builder
    public WebSocketHandlerContext(
        ObjectMapperWrapper objectMapperWrapper,
        UuidConverter uuidConverter,
        DateTimeUtil dateTimeUtil,
        @Value("${webSocketSession.expirationSeconds}") int webSocketSessionExpirationSeconds
    ) {
        this.objectMapperWrapper = objectMapperWrapper;
        this.uuidConverter = uuidConverter;
        this.dateTimeUtil = dateTimeUtil;
        this.webSocketSessionExpirationSeconds = webSocketSessionExpirationSeconds;
    }
}
