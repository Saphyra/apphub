package com.github.saphyra.apphub.service.skyxplore.game.process.cache;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
public class MessageCache extends ConcurrentHashMap<BiWrapper<UUID, BiWrapper<WebSocketEventName, Object>>, Runnable> {
    @NonNull
    private final ExecutorServiceBean executorServiceBean;

    public void add(UUID recipient, WebSocketEventName eventName, Object id, Runnable method) {
        put(new BiWrapper<>(recipient, new BiWrapper<>(eventName, id)), method);
    }

    public void process() {
        log.debug("Sending {} amount of WS messages.", size());
        if (!isEmpty()) {
            values()
                .forEach(executorServiceBean::execute);
        }
    }
}