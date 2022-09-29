package com.github.saphyra.apphub.integration.ws;

import com.github.saphyra.apphub.integration.core.TestBase;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.CollectionUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEvent;
import com.github.saphyra.apphub.integration.ws.model.WebSocketEventName;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Vector;
import java.util.function.Predicate;

@Slf4j
public class ApphubWsClient extends WebSocketClient {
    private static final ThreadLocal<List<WebSocketClient>> WS_CONNECTIONS = ThreadLocal.withInitial(Vector::new);

    @Getter
    private final List<WebSocketEvent> messages = new Vector<>();

    private final String endpoint;
    private volatile boolean opened;

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
        opened = true;
    }

    public static ApphubWsClient createAdminPanelMonitoring(Language language, UUID accessTokenId) {
        try {
            return new ApphubWsClient(language, Endpoints.WS_CONNECTION_ADMIN_PANEL_MONITORING, accessTokenId);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static ApphubWsClient createSkyXploreMainMenu(Language language, UUID accessTokenId) {
        try {
            return new ApphubWsClient(language, Endpoints.WS_CONNECTION_SKYXPLORE_MAIN_MENU, accessTokenId);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static ApphubWsClient createSkyXploreLobby(Language language, UUID accessTokenId) {
        try {
            return new ApphubWsClient(language, Endpoints.WS_CONNECTION_SKYXPLORE_LOBBY, accessTokenId);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static ApphubWsClient createSkyXploreGame(Language language, UUID accessTokenId) {
        try {
            return new ApphubWsClient(language, Endpoints.WS_CONNECTION_SKYXPLORE_GAME, accessTokenId);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<WebSocketClient> getClients() {
        return WS_CONNECTIONS.get();
    }

    public void send(WebSocketEvent event) {
        AwaitilityWrapper.createDefault()
            .until(this::isOpen)
            .assertTrue("WebSocket is not connected. Failed sending event " + event.getPayload());
        send(TestBase.OBJECT_MAPPER_WRAPPER.writeValueAsString(event));
    }

    @Override
    public void onMessage(String message) {
        if (TestBase.REST_LOGGING_ENABLED) {
            log.info("WebSocketMessage arrived to {}: {}", endpoint, message);
        }
        WebSocketEvent event = TestBase.OBJECT_MAPPER_WRAPPER.readValue(message, WebSocketEvent.class);
        messages.add(event);

        if (event.getEventName() == WebSocketEventName.PING && opened) {
            send(event);
        }
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        log.debug("WebSocket connection opened for endpoint {}", endpoint);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        opened = false;
        log.debug("WebSocket connection closed for endpoint {} with code {}, reason {}, remote {}", endpoint, code, reason, remote);
        if (code != 1000) {
            throw new RuntimeException("WebSocket connection was closed with a code " + code + " and reason " + reason);
        }
    }

    @Override
    public void onError(Exception e) {
        throw new RuntimeException("WebSocket error on endpoint " + endpoint, e);
    }

    public static void cleanUpConnections() {
        log.debug("Cleaning up connections...");
        WS_CONNECTIONS.get()
            .stream()
            .parallel()
            .forEach(WebSocketClient::close);
        WS_CONNECTIONS.remove();
    }

    public Optional<WebSocketEvent> awaitForEvent(WebSocketEventName eventName) {
        return awaitForEvent(eventName, 15);
    }

    public Optional<WebSocketEvent> awaitForEvent(WebSocketEventName eventName, int timeout) {
        return AwaitilityWrapper.findWithWait(timeout, () -> messages, webSocketEvent -> webSocketEvent.getEventName().equals(eventName));
    }

    public Optional<WebSocketEvent> awaitForEvent(WebSocketEventName eventName, Predicate<WebSocketEvent> customCondition) {
        return awaitForEvent(eventName, 15, customCondition);
    }

    public Optional<WebSocketEvent> awaitForEvent(WebSocketEventName eventName, int timeoutSeconds, Predicate<WebSocketEvent> customCondition) {
        return AwaitilityWrapper.findWithWait(timeoutSeconds, () -> messages, webSocketEvent -> webSocketEvent.getEventName().equals(eventName) && customCondition.test(webSocketEvent));
    }

    public void clearMessages() {
        log.debug("Clearing WebSocket messages for wsClient {}", endpoint);
        messages.clear();
    }
}
