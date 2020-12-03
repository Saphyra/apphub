package com.github.saphyra.apphub.lib.websocket_gateway.server;

import com.github.saphyra.apphub.lib.websocket_gateway.DefaultMessageFilter;
import com.github.saphyra.apphub.lib.websocket_gateway.MessageFilter;
import com.github.saphyra.apphub.lib.websocket_gateway.client.WsForwardClient;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * websocket server handler
 *
 * @author Jeffrey
 * @since 2017/01/22 17:33
 */
public abstract class AbstractWsServerHandler extends AbstractWebSocketHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractWsServerHandler.class);

    /**
     * channel map(key:session id, value:channel)
     */
    private static final Map<String, Channel> CHANNELS = new ConcurrentHashMap<>(100);

    private MessageFilter messageFilter = new DefaultMessageFilter();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message)
        throws Exception {
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
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus)
        throws Exception {
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
                    LOGGER.warn("Close websocket forward client error!error: {}", e);
                }
            });
        CHANNELS.remove(session.getId());
    }
}
