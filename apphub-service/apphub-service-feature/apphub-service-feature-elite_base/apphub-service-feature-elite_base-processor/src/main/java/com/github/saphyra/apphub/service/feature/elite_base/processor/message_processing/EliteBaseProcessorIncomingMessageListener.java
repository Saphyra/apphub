package com.github.saphyra.apphub.service.feature.elite_base.processor.message_processing;

import com.github.saphyra.apphub.api.feature.elite_base.message.EdMessage;
import com.github.saphyra.apphub.service.feature.elite_base.processor.message_processing.processor.EdMessageProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import static com.github.saphyra.apphub.lib.config.rabbitmq.RabbitMqConstants.MESSAGE_LISTENER_ELITE_BASE_INCOMING;

@Component(MESSAGE_LISTENER_ELITE_BASE_INCOMING)
@Slf4j
@RequiredArgsConstructor
class EliteBaseProcessorIncomingMessageListener implements MessageListener {
    private final EdMessageProcessor edMessageProcessor;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message) {
        EdMessage edMessage = objectMapper.readValue(message.getBody(), EdMessage.class);
        log.debug("Received message: {}", edMessage);
        edMessageProcessor.processMessage(edMessage);
    }
}
