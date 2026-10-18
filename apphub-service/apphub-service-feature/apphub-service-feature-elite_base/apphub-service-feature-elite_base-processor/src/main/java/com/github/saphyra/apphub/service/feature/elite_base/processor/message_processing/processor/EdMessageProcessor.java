package com.github.saphyra.apphub.service.feature.elite_base.processor.message_processing.processor;

import com.github.saphyra.apphub.api.feature.elite_base.message.EdMessage;
import com.github.saphyra.apphub.api.feature.elite_base.message.MessageStatus;
import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.monitoring.instrument.MonitoringInstruments;
import com.github.saphyra.apphub.service.feature.elite_base.processor.common.EliteBaseProcessorProperties;
import com.github.saphyra.apphub.service.feature.elite_base.processor.common.MessageProcessingDelayedException;
import com.github.saphyra.apphub.service.feature.elite_base.processor.common.PerformanceReportingKey;
import com.github.saphyra.apphub.service.feature.elite_base.processor.common.executor.MessageProcessorExecutor;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static com.github.saphyra.apphub.lib.config.rabbitmq.RabbitMqConstants.EXCHANGE_ELITE_BASE_BACKFILL;
import static com.github.saphyra.apphub.lib.config.rabbitmq.RabbitMqConstants.MESSAGE_ELITE_BASE_BACKFILL;

@Component
@RequiredArgsConstructor
@Slf4j
@Builder(access = AccessLevel.PACKAGE)
public class EdMessageProcessor {
    private final EliteBaseProcessorProperties properties;
    private final MessageProcessorExecutor messageProcessorExecutor;
    private final IdGenerator idGenerator;
    private final ErrorReporterService errorReporterService;
    private final List<MessageProcessor> messageProcessors;
    private final DateTimeUtil dateTimeUtil;
    private final MonitoringInstruments monitoringInstruments;
    private final RabbitTemplate rabbitTemplate;

    public void processMessage(EdMessage edMessage) {
        try {
            log.debug("Processing message {}: {}", edMessage.getMessageId(), edMessage.getSchemaRef());

            monitoringInstruments.wrap(
                () -> doProcessMessage(edMessage),
                Feature.ELITE_BASE_MESSAGE_PROCESSING,
                PerformanceReportingKey.PROCESS_MESSAGE.formatted(edMessage.getSchemaRef())
            );
        } catch (MessageProcessingDelayedException e) {
            processRetry(edMessage, e.getMessage());
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("violates foreign key constraint")) {
                log.debug(e.getMessage());
                processRetry(edMessage, "DataIntegrityViolation");
            } else {
                handleException(edMessage, e);
            }
        } catch (Exception e) {
            handleException(edMessage, e);
        }
    }

    private void processRetry(EdMessage edMessage, String message) {
        if (edMessage.getRetryCount() < properties.getMessageProcessorMaxRetryCount()) {
            edMessage.setRetryCount(edMessage.getRetryCount() + 1);
            log.info("Processing of message {} is delayed: {}", edMessage.getMessageId(), message);
            edMessage.setStatus(MessageStatus.ARRIVED);
            edMessage.setCreatedAt(edMessage.getCreatedAt().plus(properties.getMessageProcessorRetryDelay()));
            publish(edMessage);
        } else {
            log.warn("Retry count limit of message {} is reached. {}", edMessage, message);
            updateStatus(MessageStatus.PROCESSING_ERROR, edMessage, idGenerator.randomUuid());
        }
    }

    private void doProcessMessage(EdMessage edMessage) {
        messageProcessors.stream()
            .filter(messageProcessor -> messageProcessor.canProcess(edMessage))
            .findAny()
            .ifPresentOrElse(
                messageProcessor -> {
                    messageProcessor.processMessage(edMessage);
                    log.debug("Successfully processed message {}: {}", edMessage.getMessageId(), edMessage.getSchemaRef());
                },
                () -> {
                    if (edMessage.getSchemaRef().endsWith("/test")) {
                        log.info("Ignoring test message {} with schemaRef {}", edMessage.getMessageId(), edMessage.getSchemaRef());
                    } else {
                        log.info("Unhandled message: {}", edMessage);
                        edMessage.setStatus(MessageStatus.UNHANDLED);
                        publish(edMessage);
                    }
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
        errorReporterService.report(errorMessage, e);
        updateStatus(MessageStatus.ERROR, edMessage, exceptionId);
    }

    private void updateStatus(MessageStatus status, EdMessage edMessage, UUID exceptionId) {
        edMessage.setStatus(status);
        edMessage.setExceptionId(exceptionId);
        publish(edMessage);
    }

    private void publish(EdMessage edMessage) {
        rabbitTemplate.convertAndSend(EXCHANGE_ELITE_BASE_BACKFILL, MESSAGE_ELITE_BASE_BACKFILL, edMessage);
    }
}
