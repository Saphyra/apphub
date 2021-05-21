package com.github.saphyra.apphub.integration.backend.ws;

import com.github.saphyra.apphub.integration.backend.ws.model.WebSocketEvent;
import com.github.saphyra.apphub.integration.backend.ws.model.WebSocketEventName;
import com.github.saphyra.apphub.integration.common.TestBase;
import com.github.saphyra.apphub.integration.common.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.common.framework.BiWrapper;
import com.github.saphyra.apphub.integration.common.framework.CollectionUtils;
import com.github.saphyra.apphub.integration.common.framework.Constants;
import com.github.saphyra.apphub.integration.common.framework.Endpoints;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Vector;
import java.util.function.Predicate;

@Slf4j
public class ApphubWsClient extends WebSocketClient {
    private static final ThreadLocal<List<WebSocketClient>> WS_CONNECTIONS = ThreadLocal.withInitial(ArrayList::new);

    private final List<WebSocketEvent> messages = new Vector<>();

    private final String endpoint;

    private final Thread thread;

    private ApphubWsClient(Language language, String endpoint, UUID accessTokenId) throws URISyntaxException {
        super(
            new URI(String.format("http://localhost:%s%s", TestBase.SERVER_PORT, endpoint)),
            new Draft_6455(),
            CollectionUtils.toMap(
                new BiWrapper<>("Cookie", Constants.ACCESS_TOKEN_COOKIE + "=" + accessTokenId + "; " + Constants.LOCALE_COOKIE + "=" + language.getLocale()),
                new BiWrapper<>("Host", "localhost:" + TestBase.SERVER_PORT),
                new BiWrapper<>("Origin", "http://localhost:" + TestBase.SERVER_PORT)
            ),
            10000
        );
        this.endpoint = endpoint;
        connect();
        AwaitilityWrapper.createDefault()
            .until(this::isOpen)
            .assertTrue("Connection failed");
        WS_CONNECTIONS.get().add(this);
        thread = Thread.currentThread();
    }

    public static ApphubWsClient createSkyXploreMainMenu(Language language, UUID accessTokenId) {
        try {
            return new ApphubWsClient(language, Endpoints.CONNECTION_SKYXPLORE_MAIN_MENU, accessTokenId);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static ApphubWsClient createSkyXploreLobby(Language language, UUID accessTokenId) {
        try {
            return new ApphubWsClient(language, Endpoints.CONNECTION_SKYXPLORE_LOBBY, accessTokenId);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static ApphubWsClient createSkyXploreGame(Language language, UUID accessTokenId) {
        try {
            return new ApphubWsClient(language, Endpoints.CONNECTION_SKYXPLORE_GAME, accessTokenId);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public void send(WebSocketEvent event) {
        AwaitilityWrapper.createDefault()
            .until(this::isOpen)
            .assertTrue("WebSocket is not connected.");
        send(TestBase.OBJECT_MAPPER_WRAPPER.writeValueAsString(event));
    }

    @Override
    public void onMessage(String message) {
        log.info("WebSocketMessage arrived to {}: {}", endpoint, message);
        WebSocketEvent event = TestBase.OBJECT_MAPPER_WRAPPER.readValue(message, WebSocketEvent.class);
        messages.add(event);

        if (event.getEventName() == WebSocketEventName.PING && isOpen()) {
            send(event);
        }
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        log.info("WebSocket connection opened for endpoint {}", endpoint);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info("WebSocket connection closed for endpoint {} with code {}, reason {}, remote {}", endpoint, code, reason, remote);
        if (code != 1000) {
            thread.interrupt();
            throw new RuntimeException();
        }
    }

    @Override
    public void onError(Exception e) {
        throw new RuntimeException("WebSocket error on endpoint " + endpoint, e);
    }

    public static void cleanUpConnections() {
        WS_CONNECTIONS.get()
            .forEach(WebSocketClient::close);
        WS_CONNECTIONS.get().clear();
    }

    public Optional<WebSocketEvent> awaitForEvent(WebSocketEventName eventName) {
        return AwaitilityWrapper.findWithWait(() -> messages, webSocketEvent -> webSocketEvent.getEventName().equals(eventName));
    }

    public Optional<WebSocketEvent> awaitForEvent(WebSocketEventName eventName, Predicate<WebSocketEvent> customCondition) {
        return AwaitilityWrapper.findWithWait(() -> messages, webSocketEvent -> webSocketEvent.getEventName().equals(eventName) && customCondition.test(webSocketEvent));
    }
}
