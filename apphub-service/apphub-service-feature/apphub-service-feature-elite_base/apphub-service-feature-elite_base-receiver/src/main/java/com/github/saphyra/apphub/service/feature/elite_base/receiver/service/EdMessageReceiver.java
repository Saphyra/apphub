package com.github.saphyra.apphub.service.feature.elite_base.receiver.service;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.concurrency.ScheduledExecutorServiceBean;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.feature.elite_base.receiver.config.EliteBaseReceiverProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.zip.Inflater;

@Component
@Slf4j
@ConditionalOnProperty(value = "elite-base.messageReceiver.enabled", havingValue = "true", matchIfMissing = true)
public class EdMessageReceiver implements MessageHandler {
    private final ErrorReporterService errorReporterService;
    private final DateTimeUtil dateTimeUtil;
    private final EliteBaseReceiverProperties eliteBaseReceiverProperties;
    private final ScheduledExecutorServiceBean scheduledExecutorServiceBean;
    private final EdMessageHandlerHelper edMessageHandlerHelper;

    private volatile LocalDateTime lastMessage;

    EdMessageReceiver(
        ErrorReporterService errorReporterService,
        DateTimeUtil dateTimeUtil,
        ScheduledExecutorServiceBean scheduledExecutorServiceBean,
        EliteBaseReceiverProperties eliteBaseReceiverProperties,
        EdMessageHandlerHelper edMessageHandlerHelper
    ) {
        this.errorReporterService = errorReporterService;
        this.dateTimeUtil = dateTimeUtil;
        lastMessage = dateTimeUtil.getCurrentDateTime();
        this.eliteBaseReceiverProperties = eliteBaseReceiverProperties;
        this.scheduledExecutorServiceBean = scheduledExecutorServiceBean;
        this.edMessageHandlerHelper = edMessageHandlerHelper;
    }

    @Override
    @ServiceActivator(inputChannel = "zeroMqChannel")
    public void handleMessage(Message<?> message) throws MessagingException {
        try {
            byte[] output = new byte[256 * 1024];
            byte[] payload = (byte[]) message.getPayload();
            Inflater inflater = new Inflater();
            inflater.setInput(payload);

            lastMessage = dateTimeUtil.getCurrentDateTime();

            int outputLength = inflater.inflate(output);
            String outputString = new String(output, 0, outputLength, StandardCharsets.UTF_8);
            log.debug("{}", outputString);

            edMessageHandlerHelper.handleMessage(outputString);
        } catch (Exception e) {
            errorReporterService.report("Failed processing message", e);
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    void scheduleChecked() {
        log.info("Starting scheduled check for incoming messages. Check interval: {}", eliteBaseReceiverProperties.getIncomingMessageCheckInterval());
        scheduledExecutorServiceBean.scheduleFixedRate(this::shutdownIfTimeout, eliteBaseReceiverProperties.getIncomingMessageCheckInterval());
    }

    private void shutdownIfTimeout() {
        if (edMessageHandlerHelper.shutdownNeeded(lastMessage)) {
            edMessageHandlerHelper.shutdown();
            System.exit(0);
        }
    }
}
