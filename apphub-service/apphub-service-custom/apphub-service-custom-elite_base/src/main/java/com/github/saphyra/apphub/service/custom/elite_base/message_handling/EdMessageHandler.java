package com.github.saphyra.apphub.service.custom.elite_base.message_handling;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.concurrency.ScheduledExecutorServiceBean;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.custom.elite_base.common.EliteBaseProperties;
import com.github.saphyra.apphub.service.custom.elite_base.common.MessageProcessingDelayedException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@ConditionalOnProperty(value = "elite-base.messageHandler.enabled", havingValue = "true", matchIfMissing = true)
public class EdMessageHandler implements MessageHandler {
    private final ErrorReporterService errorReporterService;
    private final DateTimeUtil dateTimeUtil;
    private final EliteBaseProperties eliteBaseProperties;
    private final ScheduledExecutorServiceBean scheduledExecutorServiceBean;
    private final EdMessageHandlerHelper edMessageHandlerHelper;

    private volatile LocalDateTime lastMessage;

    public EdMessageHandler(
        ErrorReporterService errorReporterService,
        DateTimeUtil dateTimeUtil,
        ScheduledExecutorServiceBean scheduledExecutorServiceBean,
        EliteBaseProperties eliteBaseProperties,
        EdMessageHandlerHelper edMessageHandlerHelper
    ) {
        this.errorReporterService = errorReporterService;
        this.dateTimeUtil = dateTimeUtil;
        lastMessage = dateTimeUtil.getCurrentDateTime();
        this.eliteBaseProperties = eliteBaseProperties;
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
        } catch (MessageProcessingDelayedException e) {
            log.warn(e.getMessage());
        } catch (Exception e) {
            errorReporterService.report("Failed processing message", e);
            log.error("Failed processing message", e);
        }
    }

    @PostConstruct
    void scheduleChecked() {
        scheduledExecutorServiceBean.scheduleFixedRate(this::shutdownIfTimeout, eliteBaseProperties.getIncomingMessageCheckInterval());
    }

    private void shutdownIfTimeout() {
        if (edMessageHandlerHelper.shutdownNeeded(lastMessage)) {
            edMessageHandlerHelper.shutdown();
            System.exit(0);
        }
    }
}
