package com.github.saphyra.apphub.service.custom.elite_base.message_handling;

import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.custom.elite_base.common.BufferSynchronizationService;
import com.github.saphyra.apphub.service.custom.elite_base.common.EliteBaseProperties;
import com.github.saphyra.apphub.service.custom.elite_base.common.MessageProcessingLock;
import com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao.MessageDao;
import com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao.MessageFactory;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.processor.EdMessageProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.locks.Lock;

@Component
@RequiredArgsConstructor
@Slf4j
class EdMessageHandlerHelper {
    private final MessageFactory messageFactory;
    private final EdMessageProcessor edMessageProcessor;
    private final MessageProcessingLock messageProcessingLock;
    private final MessageDao messageDao;
    private final DateTimeUtil dateTimeUtil;
    private final EliteBaseProperties eliteBaseProperties;
    private final ErrorReporterService errorReporterService;
    private final ApplicationContextProxy applicationContextProxy;
    private final ExecutorServiceBean executorServiceBean;
    private final BufferSynchronizationService bufferSynchronizationService;

    void handleMessage(String outputString) {
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
    }

    public boolean shutdownNeeded(LocalDateTime lastMessage) {
        log.debug("Checking connection...");
        LocalDateTime currentTime = dateTimeUtil.getCurrentDateTime();
        LocalDateTime expirationTime = currentTime
            .minus(eliteBaseProperties.getIncomingMessageTimeout());

        log.info("Last message arrived at {}. Current time: {}, Expiration time: {}", lastMessage, currentTime, expirationTime);

        boolean result = expirationTime.isAfter(lastMessage);
        if (result) {
            log.error("Last message arrived at {}. Shutting down service...", lastMessage);
            errorReporterService.report(String.format("Last message came at %s. Shutting down service...", lastMessage));
        }
        return result;
    }

    public void shutdown() {
        Lock writeLock = messageProcessingLock.writeLock();
        writeLock.lock();
        try {
            bufferSynchronizationService.synchronizeAll();
        } catch (Exception e) {
            errorReporterService.report("Failed saving buffers before shutting down", e);
        }

        ConfigurableApplicationContext applicationContext  =applicationContextProxy.getContext();
        applicationContext.registerShutdownHook();
        applicationContext.close();
    }
}
