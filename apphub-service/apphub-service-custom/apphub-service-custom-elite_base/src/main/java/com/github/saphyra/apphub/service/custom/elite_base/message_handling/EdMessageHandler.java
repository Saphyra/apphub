package com.github.saphyra.apphub.service.custom.elite_base.message_handling;

import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.ScheduledExecutorServiceBean;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.custom.elite_base.common.BufferSynchronizationService;
import com.github.saphyra.apphub.service.custom.elite_base.common.EliteBaseProperties;
import com.github.saphyra.apphub.service.custom.elite_base.common.MessageProcessingDelayedException;
import com.github.saphyra.apphub.service.custom.elite_base.common.MessageProcessingLock;
import com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao.MessageDao;
import com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao.MessageFactory;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.processor.EdMessageProcessor;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.concurrent.locks.Lock;
import java.util.zip.Inflater;

@Component
@Slf4j
@ConditionalOnProperty(value = "elite-base.messageHandler.enabled", havingValue = "true", matchIfMissing = true)
public class EdMessageHandler implements MessageHandler {
    private final MessageFactory messageFactory;
    private final ErrorReporterService errorReporterService;
    private final DateTimeUtil dateTimeUtil;
    private final EliteBaseProperties eliteBaseProperties;
    private final ScheduledExecutorServiceBean scheduledExecutorServiceBean;
    private final ApplicationContextProxy applicationContextProxy;
    private final EdMessageProcessor edMessageProcessor;
    private final MessageProcessingLock messageProcessingLock;
    private final MessageDao messageDao;
    private final ExecutorServiceBean executorServiceBean;
    private final BufferSynchronizationService bufferSynchronizationService;

    private volatile LocalDateTime lastMessage;

    public EdMessageHandler(
        MessageFactory messageFactory,
        ErrorReporterService errorReporterService,
        DateTimeUtil dateTimeUtil,
        ScheduledExecutorServiceBean scheduledExecutorServiceBean,
        EliteBaseProperties eliteBaseProperties,
        ApplicationContextProxy applicationContextProxy,
        EdMessageProcessor edMessageProcessor,
        MessageProcessingLock messageProcessingLock,
        MessageDao messageDao,
        ExecutorServiceBean executorServiceBean,
        BufferSynchronizationService bufferSynchronizationService
    ) {
        this.messageFactory = messageFactory;
        this.errorReporterService = errorReporterService;
        this.dateTimeUtil = dateTimeUtil;
        lastMessage = dateTimeUtil.getCurrentDateTime();
        this.eliteBaseProperties = eliteBaseProperties;
        this.scheduledExecutorServiceBean = scheduledExecutorServiceBean;
        this.applicationContextProxy = applicationContextProxy;
        this.edMessageProcessor = edMessageProcessor;
        this.messageProcessingLock = messageProcessingLock;
        this.messageDao = messageDao;
        this.executorServiceBean = executorServiceBean;
        this.bufferSynchronizationService = bufferSynchronizationService;
    }

    @Override
    @ServiceActivator(inputChannel = "zeroMqChannel")
    public void handleMessage(Message<?> message) throws MessagingException {
        byte[] output = new byte[256 * 1024];
        byte[] payload = (byte[]) message.getPayload();
        Inflater inflater = new Inflater();
        inflater.setInput(payload);
        try {
            lastMessage = dateTimeUtil.getCurrentDateTime();
            int outputLength = inflater.inflate(output);
            String outputString = new String(output, 0, outputLength, StandardCharsets.UTF_8);
            log.debug("{}", outputString);
            executorServiceBean.execute(() -> {
                EdMessage edMessage = messageFactory.create(outputString);
                Lock readLock = messageProcessingLock.readLock();
                if (readLock.tryLock()) {
                    try {
                        edMessageProcessor.processMessage(edMessage);
                    } finally {
                        readLock.unlock();
                    }
                } else {
                    messageDao.save(edMessage);
                }
            });
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
        log.debug("Checking connection...");
        LocalDateTime currentTime = dateTimeUtil.getCurrentDateTime();
        LocalDateTime expirationTime = currentTime
            .minus(eliteBaseProperties.getIncomingMessageTimeout());

        log.info("Last message arrived at {}. Current time: {}, Expiration time: {}", lastMessage, currentTime, expirationTime);

        if (expirationTime.isAfter(lastMessage)) {
            log.error("Last message arrived at {}. Shutting down service...", lastMessage);
            errorReporterService.report(String.format("Last message came at %s. Shutting down service...", lastMessage));
            shutdown();
        }
    }

    private void shutdown() {
        Lock writeLock = messageProcessingLock.writeLock();
        writeLock.lock();
        try {
            bufferSynchronizationService.synchronizeAll();
        } catch (Exception e) {
            errorReporterService.report("Failed saving buffers before shutting down", e);
        }
        SpringApplication.exit(applicationContextProxy.getContext(), () -> 0);

        System.exit(0);
    }
}
