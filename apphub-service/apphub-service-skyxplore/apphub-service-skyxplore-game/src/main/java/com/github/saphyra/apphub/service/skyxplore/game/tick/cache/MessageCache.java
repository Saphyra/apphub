package com.github.saphyra.apphub.service.skyxplore.game.tick.cache;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

//TODO unit test
public class MessageCache {
    private final Map<BiWrapper<UUID, BiWrapper<WebSocketEventName, Object>>, Runnable> messages = new ConcurrentHashMap<>();

    public void add(UUID recipient, WebSocketEventName eventName, Object id, Runnable method) {
        messages.put(new BiWrapper<>(recipient, new BiWrapper<>(eventName, id)), method);
    }

    public void process(ExecutorServiceBean executorServiceBean) {
        if (!messages.isEmpty()) {
            messages.values()
                .forEach(executorServiceBean::execute);
        }
    }
}
