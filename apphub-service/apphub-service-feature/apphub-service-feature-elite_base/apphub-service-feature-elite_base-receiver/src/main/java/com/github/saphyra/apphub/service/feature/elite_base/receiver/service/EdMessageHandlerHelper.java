package com.github.saphyra.apphub.service.feature.elite_base.receiver.service;

import com.github.saphyra.apphub.api.feature.elite_base.message.EdMessage;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.feature.elite_base.receiver.config.EliteBaseReceiverProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.github.saphyra.apphub.lib.config.rabbitmq.RabbitMqConstants.EXCHANGE_ELITE_BASE_INCOMING;
import static com.github.saphyra.apphub.lib.config.rabbitmq.RabbitMqConstants.MESSAGE_ELITE_BASE_INCOMING;

@Component
@RequiredArgsConstructor
@Slf4j
class EdMessageHandlerHelper {
    private final MessageFactory messageFactory;
    private final DateTimeUtil dateTimeUtil;
    private final EliteBaseReceiverProperties eliteBaseReceiverProperties;
    private final ErrorReporterService errorReporterService;
    private final ApplicationContextProxy applicationContextProxy;
    private final RabbitTemplate rabbitTemplate;

    void handleMessage(String message) {
        EdMessage edMessage = messageFactory.create(message);

        rabbitTemplate.convertAndSend(EXCHANGE_ELITE_BASE_INCOMING, MESSAGE_ELITE_BASE_INCOMING, edMessage);
    }

    public boolean shutdownNeeded(LocalDateTime lastMessage) {
        if (!eliteBaseReceiverProperties.isShutdownEnabled()) {
            return false;
        }

        log.debug("Checking connection...");
        LocalDateTime currentTime = dateTimeUtil.getCurrentDateTime();
        LocalDateTime expirationTime = currentTime
            .minus(eliteBaseReceiverProperties.getIncomingMessageTimeout());

        log.info("Last message arrived at {}. Current time: {}, Expiration time: {}", lastMessage, currentTime, expirationTime);

        boolean result = expirationTime.isAfter(lastMessage);
        if (result) {
            errorReporterService.report(String.format("Last message came at %s. Shutting down service...", lastMessage));
        }
        return result;
    }

    public void shutdown() {
        ConfigurableApplicationContext applicationContext = applicationContextProxy.getContext();
        applicationContext.registerShutdownHook();
        applicationContext.close();
    }
}
