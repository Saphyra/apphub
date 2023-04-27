package com.github.saphyra.apphub.service.skyxplore.game.common.ws;

import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.lib.skyxplore.ws.SkyXploreWsEvent;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Vector;
import java.util.function.Predicate;

@Slf4j
public class SkyXploreWsClient extends WebSocketClient {
    private final ObjectMapperWrapper objectMapperWrapper;
    private final SleepService sleepService;

    private final List<SkyXploreWsEvent> messages = new Vector<>();

    @Builder
    SkyXploreWsClient(String url, ObjectMapperWrapper objectMapperWrapper, SleepService sleepService) throws Exception {
        super(
            new URI(url),
            new Draft_6455(),
            Collections.emptyMap(),
            10000
        );
        this.objectMapperWrapper = objectMapperWrapper;
        this.sleepService = sleepService;
        connect();

        for (int i = 0; i < 10000; i++) {
            if (isOpen()) {
                return;
            }

            sleepService.sleep(10);
        }

        throw new IllegalStateException("WebSocket not connected.");
    }

    public void send(SkyXploreWsEvent event) {
        send(objectMapperWrapper.writeValueAsString(event));
    }

    @Override
    public void onMessage(String message) {
        log.debug("Message arrived: {}", message);
        SkyXploreWsEvent event = objectMapperWrapper.readValue(message, SkyXploreWsEvent.class);
        synchronized (messages) {
            messages.add(event);
        }
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        if (code != 1000) {
            throw new RuntimeException("WebSocket connection was closed with a code " + code + " and reason " + reason);
        }
    }

    @Override
    public void onError(Exception e) {
        throw new RuntimeException("WebSocket error", e);
    }

    public SkyXploreWsEvent awaitForEvent(Predicate<SkyXploreWsEvent> customCondition) {
        while (true) {
            Optional<SkyXploreWsEvent> found;

            synchronized (messages) {
                found = messages.stream()
                    .filter(customCondition)
                    .findFirst();

                if (found.isPresent()) {
                    SkyXploreWsEvent result = found.get();
                    messages.remove(result);
                    return result;
                }
            }

            sleepService.sleep(1);
        }
    }

    public void clearMessages(){
        messages.clear();
    }
}
