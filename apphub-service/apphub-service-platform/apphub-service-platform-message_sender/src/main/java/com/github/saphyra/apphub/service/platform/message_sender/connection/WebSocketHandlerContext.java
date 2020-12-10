package com.github.saphyra.apphub.service.platform.message_sender.connection;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Getter
public class WebSocketHandlerContext {
    private final ObjectMapperWrapper objectMapperWrapper;
    private final UuidConverter uuidConverter;
    private final DateTimeUtil dateTimeUtil;

    @Value("${webSocketSession.expirationSeconds}")
    private int webSocketSessionExpirationSeconds;
}
