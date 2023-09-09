package com.github.saphyra.apphub.lib.web_socket;

import com.github.saphyra.apphub.lib.event.processor.EnableEventProcessor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@EnableWebSocket
@ComponentScan
@EnableEventProcessor
public class WebSocketConfiguration {
}
