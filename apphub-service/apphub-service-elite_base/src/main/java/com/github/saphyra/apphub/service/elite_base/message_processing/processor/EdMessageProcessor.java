package com.github.saphyra.apphub.service.elite_base.message_processing.processor;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.concurrency.FixedExecutorServiceBean;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.elite_base.common.EliteBaseProperties;
import com.github.saphyra.apphub.service.elite_base.common.MessageProcessingDelayedException;
import com.github.saphyra.apphub.service.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.elite_base.message_handling.dao.MessageDao;
import com.github.saphyra.apphub.service.elite_base.message_handling.dao.MessageStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
@Builder(access = AccessLevel.PACKAGE)
public class EdMessageProcessor {
    private final EliteBaseProperties properties;
    private final MessageDao messageDao;
    private final FixedExecutorServiceBean executorServiceBean;
    private final IdGenerator idGenerator;
    private final ErrorReporterService errorReporterService;
    private final List<MessageProcessor> messageProcessors;
    private final DateTimeUtil dateTimeUtil;

    @SneakyThrows
    public synchronized void processMessages() {
        StopWatch stopWatch = StopWatch.createStarted();
        List<EdMessage> messages = messageDao.getMessages(dateTimeUtil.getCurrentDateTime(), properties.getMessageProcessorBatchSize());
        log.info("Processing {} messages.", messages.size());

        List<List<EdMessage>> partitions = ListUtils.partition(messages, properties.getMessageProcessorSublistSize());

        List<Future<ExecutionResult<Void>>> futures = partitions.stream()
            .map(edMessages -> executorServiceBean.execute(() -> processMessages(edMessages)))
            .toList();

        for (Future<ExecutionResult<Void>> future : futures) {
            future.get();
        }

        stopWatch.stop();
        log.info("{} messages processed in {}ms", messages.size(), stopWatch.getTime(TimeUnit.MILLISECONDS));
    }

    private void processMessages(List<EdMessage> edMessages) {
        StopWatch stopWatch = StopWatch.createStarted();
        edMessages.forEach(this::processMessage);
        stopWatch.stop();
        log.debug("Processed {} messages in {}ms.", edMessages.size(), stopWatch.getTime(TimeUnit.MILLISECONDS));
    }

    private void processMessage(EdMessage edMessage) {
        try {
            messageProcessors.stream()
                .filter(messageProcessor -> messageProcessor.canProcess(edMessage))
                .findAny()
                .ifPresentOrElse(
                    messageProcessor -> {
                        messageProcessor.processMessage(edMessage);
                        edMessage.setStatus(MessageStatus.PROCESSED);
                        messageDao.save(edMessage);
                    },
                    () -> {
                        log.info("Unhandled message: {}", edMessage);
                        edMessage.setStatus(MessageStatus.UNHANDLED);
                        messageDao.save(edMessage);
                    }
                );
        } catch (MessageProcessingDelayedException e) {
            if (edMessage.getRetryCount() < properties.getMaxRetryCount()) {
                edMessage.setRetryCount(edMessage.getRetryCount() + 1);
                log.warn("Processing of message {} is delayed: {}", edMessage, e.getMessage());
                edMessage.setStatus(MessageStatus.ARRIVED);
                edMessage.setCreatedAt(edMessage.getCreatedAt().plus(properties.getRetryDelay()));
                messageDao.save(edMessage);
            } else {
                log.warn("Retry count limit of message {} is reached.", edMessage);
                handleException(MessageStatus.PROCESSING_ERROR, edMessage, e);
            }
        } catch (Exception e) {
            handleException(MessageStatus.ERROR, edMessage, e);
        }
    }

    private void handleException(MessageStatus status, EdMessage edMessage, Exception e) {
        UUID exceptionId = idGenerator.randomUuid();
        String errorMessage = "Failed processing message %s: %s. ExceptionId: %s. Schema: %s".formatted(
            edMessage.getMessageId(),
            e.getMessage(),
            exceptionId,
            edMessage.getSchemaRef()
        );
        log.error(errorMessage, e);
        if(status != MessageStatus.PROCESSING_ERROR){
            errorReporterService.report(errorMessage, e);
        }
        edMessage.setStatus(status);
        edMessage.setExceptionId(exceptionId);
        messageDao.save(edMessage);
    }
}
