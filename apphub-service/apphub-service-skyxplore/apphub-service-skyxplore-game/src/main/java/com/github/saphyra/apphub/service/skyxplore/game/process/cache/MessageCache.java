package com.github.saphyra.apphub.service.skyxplore.game.process.cache;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class MessageCache extends ConcurrentHashMap<BiWrapper<UUID, BiWrapper<WebSocketEventName, Object>>, Runnable> {
    public void add(UUID recipient, WebSocketEventName eventName, Object id, Runnable method) {
        put(new BiWrapper<>(recipient, new BiWrapper<>(eventName, id)), method);
    }

    public void process(ExecutorServiceBean executorServiceBean) {
        log.info("Sending {} amount of WS messages.", size()); //TODO log level
        if (!isEmpty()) {
            values()
                .forEach(executorServiceBean::execute);
        }
    }
}
