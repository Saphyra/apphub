package com.github.saphyra.apphub.service.skyxplore.data.ws;

import com.github.saphyra.apphub.lib.common_util.AbstractCache;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class WebSocketSessionCache extends AbstractCache<UUID, WebSocketSession> {
    public WebSocketSessionCache() {
        super(CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.SECONDS).build());
    }

    @Override
    protected Optional<WebSocketSession> load(UUID key) {
        throw ExceptionFactory.forbiddenOperation("WebSocketSessionCache cannot load itself.");
    }
}
