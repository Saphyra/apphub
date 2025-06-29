package com.github.saphyra.apphub.service.custom.elite_base.message_processing.processor;

import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopic;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.performance_reporting.PerformanceReporter;
import com.github.saphyra.apphub.service.custom.elite_base.common.EliteBaseProperties;
import com.github.saphyra.apphub.service.custom.elite_base.common.MessageProcessingDelayedException;
import com.github.saphyra.apphub.service.custom.elite_base.common.PerformanceReportingKey;
import com.github.saphyra.apphub.service.custom.elite_base.common.executor.MessageProcessorExecutor;
import com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao.MessageDao;
import com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao.MessageStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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
    private final MessageProcessorExecutor messageProcessorExecutor;
    private final IdGenerator idGenerator;
    private final ErrorReporterService errorReporterService;
    private final List<MessageProcessor> messageProcessors;
    private final DateTimeUtil dateTimeUtil;
    private final PerformanceReporter performanceReporter;

    @SneakyThrows
    public synchronized void processMessages() {
        performanceReporter.wrap(
            () -> {
                StopWatch stopWatch = StopWatch.createStarted();
                List<EdMessage> messages = doProcessMessages();

                stopWatch.stop();
                if (!messages.isEmpty()) {
                    log.info("{} messages processed in {}ms", messages.size(), stopWatch.getTime(TimeUnit.MILLISECONDS));
                }
            },
            PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING,
            PerformanceReportingKey.PROCESS_BATCH.name()
        );
    }

    @SneakyThrows
    private List<EdMessage> doProcessMessages() {
        List<EdMessage> messages = performanceReporter.wrap(
            () -> messageDao.getMessages(dateTimeUtil.getCurrentDateTime(), properties.getMessageProcessorBatchSize()),
            PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING,
            PerformanceReportingKey.QUERY_ARRIVED_MESSAGES.name()
        );
        if (messages.isEmpty()) {
            return messages;
        }
        log.info("Processing {} messages.", messages.size());

        List<Future<ExecutionResult<Void>>> futures = messages.stream()
            .map(edMessages -> messageProcessorExecutor.execute(() -> processMessage(edMessages)))
            .toList();

        for (Future<ExecutionResult<Void>> future : futures) {
            future.get();
        }
        return messages;
    }

    public void processMessage(EdMessage edMessage) {
        try {
            log.info("Processing message {}: {}", edMessage.getMessageId(), edMessage.getSchemaRef());

            performanceReporter.wrap(
                () -> doProcessMessage(edMessage),
                PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING,
                PerformanceReportingKey.PROCESS_MESSAGE.formatted(edMessage.getSchemaRef())
            );
        } catch (MessageProcessingDelayedException e) {
            if (edMessage.getRetryCount() < properties.getMessageProcessorMaxRetryCount()) {
                edMessage.setRetryCount(edMessage.getRetryCount() + 1);
                log.info("Processing of message {} is delayed: {}", edMessage.getMessageId(), e.getMessage());
                edMessage.setStatus(MessageStatus.ARRIVED);
                edMessage.setCreatedAt(edMessage.getCreatedAt().plus(properties.getMessageProcessorRetryDelay()));
                messageDao.save(edMessage);
            } else {
                log.warn("Retry count limit of message {} is reached. {}", edMessage, e.getMessage());
                updateStatus(MessageStatus.PROCESSING_ERROR, edMessage, idGenerator.randomUuid());
            }
        } catch (Exception e) {
            handleException(edMessage, e);
        }
    }

    private void doProcessMessage(EdMessage edMessage) {
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
                    if (edMessage.getSchemaRef().endsWith("/test")) {
                        log.info("Processing test message: {}", edMessage);
                        edMessage.setStatus(MessageStatus.PROCESSED);
                    } else {
                        log.info("Unhandled message: {}", edMessage);
                        edMessage.setStatus(MessageStatus.UNHANDLED);
                    }

                    messageDao.save(edMessage);
                }
            );
    }

    private void handleException(EdMessage edMessage, Exception e) {
        UUID exceptionId = idGenerator.randomUuid();
        String errorMessage = "Failed processing message %s: %s. ExceptionId: %s. Schema: %s".formatted(
            edMessage.getMessageId(),
            e.getMessage(),
            exceptionId,
            edMessage.getSchemaRef()
        );
        log.error(errorMessage, e);
        errorReporterService.report(errorMessage, e);
        updateStatus(MessageStatus.ERROR, edMessage, exceptionId);
    }

    private void updateStatus(MessageStatus status, EdMessage edMessage, UUID exceptionId) {
        edMessage.setStatus(status);
        edMessage.setExceptionId(exceptionId);
        messageDao.save(edMessage);
    }
}
