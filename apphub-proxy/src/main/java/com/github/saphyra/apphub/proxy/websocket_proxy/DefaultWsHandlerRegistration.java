package com.github.saphyra.apphub.proxy.websocket_proxy;

import com.github.saphyra.apphub.proxy.websocket_proxy.server.AbstractWsServerHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * default websocket handler registration
 *
 * @author Jeffrey
 * @since 2017/12/04 18:47
 */
public class DefaultWsHandlerRegistration implements WsHandlerRegistration {

    private final Map<String, AbstractWsServerHandler> handlerMap = Collections.synchronizedMap(new HashMap<>(5));

    private final Map<String, HandshakeInterceptor> interceptorMap = Collections.synchronizedMap(new HashMap<>(5));

    @Override
    public void addHandler(AbstractWsServerHandler... handlers) {
        Arrays.stream(handlers)
            .forEach(handler -> handlerMap.putIfAbsent(handler.getClass().getName(), handler));
    }

    @Override
    public void addInterceptor(HandshakeInterceptor... interceptors) {
        Arrays.stream(interceptors)
            .forEach(interceptor -> interceptorMap
                .putIfAbsent(interceptor.getClass().getName(), interceptor));
    }

    @Override
    public AbstractWsServerHandler getHandler(String handlerClassName) {
        return handlerMap.get(handlerClassName);
    }

    @Override
    public HandshakeInterceptor getInterceptor(String interceptorClassName) {
        return interceptorMap.get(interceptorClassName);
    }
}
