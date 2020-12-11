package com.github.saphyra.apphub.proxy.websocket_proxy.server;

import com.github.saphyra.apphub.proxy.websocket_proxy.DefaultMessageFilter;
import com.github.saphyra.apphub.proxy.websocket_proxy.MessageFilter;
import com.github.saphyra.apphub.proxy.websocket_proxy.client.WsForwardClient;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class AbstractWsServerHandler extends AbstractWebSocketHandler {

    private static final Map<String, Channel> CHANNELS = new ConcurrentHashMap<>(100);

    private MessageFilter messageFilter = new DefaultMessageFilter();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        Optional.ofNullable(messageFilter.fromMessage(message))
            .ifPresent(msg -> CHANNELS.get(session.getId()).writeAndFlush(msg));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        WsForwardClient client = WsForwardClient.create(getForwardUrl(session), session, messageFilter, session.getHandshakeHeaders());
        client.connect();
        Channel channel = client.getChannel();
        CHANNELS.put(session.getId(), channel);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception)
        throws Exception {
        if (session.isOpen()) {
            session.close();
        }
        closeGracefully(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        closeGracefully(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * get forward url
     *
     * @param session websocket session
     * @return forward url
     */
    public abstract String getForwardUrl(WebSocketSession session);

    public MessageFilter getMessageFilter() {
        return messageFilter;
    }

    public void setMessageFilter(MessageFilter messageFilter) {
        this.messageFilter = messageFilter;
    }

    /**
     * close client
     *
     * @param session WebSocketSession
     */
    private void closeGracefully(WebSocketSession session) {
        Optional.ofNullable(CHANNELS.get(session.getId()))
            .ifPresent(channel -> {
                try {
                    if (channel.isOpen()) {
                        channel.close();
                    }
                } catch (Exception e) {
                    log.warn("Close websocket forward client error!error", e);
                }
            });
        CHANNELS.remove(session.getId());
    }
}
