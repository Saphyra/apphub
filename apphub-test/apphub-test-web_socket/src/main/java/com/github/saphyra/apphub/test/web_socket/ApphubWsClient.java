package com.github.saphyra.apphub.test.web_socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.Base64Encoder;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.converter.AccessTokenHeaderConverter;
import com.github.saphyra.apphub.test.common.AwaitilityWrapper;
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
    private static final ObjectMapperWrapper OBJECT_MAPPER_WRAPPER = new ObjectMapperWrapper(new ObjectMapper());
    private static final AccessTokenHeaderConverter ACCESS_TOKEN_HEADER_CONVERTER = new AccessTokenHeaderConverter(
        new Base64Encoder(),
        OBJECT_MAPPER_WRAPPER
    );

    @Getter
    private final List<WebSocketEvent> messages = new Vector<>();

    private final String endpoint;
    private volatile boolean opened;

    public ApphubWsClient(int port, String endpoint, UUID accessTokenId, String locale) throws URISyntaxException {
        super(
            new URI(String.format("ws://localhost:%s%s", port, endpoint)),
            new Draft_6455(),
            CollectionUtils.toMap(
                new BiWrapper<>("Cookie", String.format("%s=%s", Constants.ACCESS_TOKEN_COOKIE, accessTokenId)),
                new BiWrapper<>(Constants.LOCALE_HEADER, locale),
                new BiWrapper<>(Constants.ACCESS_TOKEN_HEADER, ACCESS_TOKEN_HEADER_CONVERTER.convertDomain(AccessTokenHeader.builder().accessTokenId(accessTokenId).build())),
                new BiWrapper<>("Host", "localhost:" + port),
                new BiWrapper<>("Origin", "http://localhost:" + port)
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

    public static List<WebSocketClient> getClients() {
        return WS_CONNECTIONS.get();
    }

    public void send(WebSocketEvent event) {
        AwaitilityWrapper.createDefault()
            .until(this::isOpen)
            .assertTrue("WebSocket is not connected. Failed sending event " + event.getPayload());
        send(OBJECT_MAPPER_WRAPPER.writeValueAsString(event));
    }

    @Override
    public void onMessage(String message) {
        log.info("WebSocketMessage arrived to {}: {}", endpoint, message);
        WebSocketEvent event = OBJECT_MAPPER_WRAPPER.readValue(message, WebSocketEvent.class);
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
