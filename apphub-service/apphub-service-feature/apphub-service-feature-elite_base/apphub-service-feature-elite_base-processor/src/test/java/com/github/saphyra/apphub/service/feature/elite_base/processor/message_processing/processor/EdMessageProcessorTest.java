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
import com.github.saphyra.apphub.service.feature.elite_base.processor.common.executor.MessageProcessorExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.github.saphyra.apphub.lib.config.rabbitmq.RabbitMqConstants.EXCHANGE_ELITE_BASE_BACKFILL;
import static com.github.saphyra.apphub.lib.config.rabbitmq.RabbitMqConstants.MESSAGE_ELITE_BASE_BACKFILL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class EdMessageProcessorTest {
    private static final String SCHEMA_REF = "schema-ref";
    private static final String TEST_SCHEMA_REF = "schema-ref/test";
    private static final int RETRY_COUNT = 3;
    private static final Duration RETRY_DELAY = Duration.ofHours(2);
    private static final LocalDateTime CREATED_AT = LocalDateTime.now();
    private static final UUID EXCEPTION_ID = UUID.randomUUID();

    @Mock
    private EliteBaseProcessorProperties properties;

    @Mock
    private MessageProcessorExecutor messageProcessorExecutor;

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private ErrorReporterService errorReporterService;

    @Mock
    private MessageProcessor messageProcessor;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private MonitoringInstruments monitoringInstruments;

    @Mock
    private RabbitTemplate rabbitTemplate;

    private EdMessageProcessor underTest;

    @Mock
    private EdMessage message;

    @BeforeEach
    void setUp() {
        given(message.getSchemaRef()).willReturn(SCHEMA_REF);
        doAnswer(invocation -> {
            invocation.getArgument(0, Runnable.class).run();
            return null;
        })
            .when(monitoringInstruments)
            .wrap(any(Runnable.class), eq(Feature.ELITE_BASE_MESSAGE_PROCESSING), any());

        underTest = EdMessageProcessor.builder()
            .properties(properties)
            .messageProcessorExecutor(messageProcessorExecutor)
            .idGenerator(idGenerator)
            .errorReporterService(errorReporterService)
            .messageProcessors(List.of(messageProcessor))
            .dateTimeUtil(dateTimeUtil)
            .monitoringInstruments(monitoringInstruments)
            .rabbitTemplate(rabbitTemplate)
            .build();
    }

    @Test
    void successfulProcessing() {
        given(messageProcessor.canProcess(message)).willReturn(true);

        underTest.processMessage(message);

        then(messageProcessor).should().processMessage(message);
    }

    @Test
    void processTestSchema() {
        given(messageProcessor.canProcess(message)).willReturn(false);
        given(message.getSchemaRef()).willReturn(TEST_SCHEMA_REF);

        underTest.processMessage(message);

        then(rabbitTemplate).shouldHaveNoInteractions();
    }

    @Test
    void unprocessedSchema() {
        given(messageProcessor.canProcess(message)).willReturn(false);

        underTest.processMessage(message);

        then(message).should().setStatus(MessageStatus.UNHANDLED);
        then(rabbitTemplate).should().convertAndSend(EXCHANGE_ELITE_BASE_BACKFILL, MESSAGE_ELITE_BASE_BACKFILL, message);
    }

    @Test
    void retryDelayedMessage() {
        given(messageProcessor.canProcess(message)).willReturn(true);
        doThrow(new MessageProcessingDelayedException("asd"))
            .when(messageProcessor)
            .processMessage(message);
        given(properties.getMessageProcessorMaxRetryCount()).willReturn(RETRY_COUNT);
        given(properties.getMessageProcessorRetryDelay()).willReturn(RETRY_DELAY);
        given(message.getRetryCount()).willReturn(RETRY_COUNT - 2);
        given(message.getCreatedAt()).willReturn(CREATED_AT);

        underTest.processMessage(message);

        then(message).should().setStatus(MessageStatus.ARRIVED);
        then(message).should().setRetryCount(RETRY_COUNT - 1);
        then(message).should().setCreatedAt(CREATED_AT.plus(RETRY_DELAY));
        then(rabbitTemplate).should().convertAndSend(EXCHANGE_ELITE_BASE_BACKFILL, MESSAGE_ELITE_BASE_BACKFILL, message);
    }

    @Test
    void noMoreRetries() {
        given(messageProcessor.canProcess(message)).willReturn(true);
        doThrow(new MessageProcessingDelayedException("asd"))
            .when(messageProcessor)
            .processMessage(message);
        given(properties.getMessageProcessorMaxRetryCount()).willReturn(RETRY_COUNT);
        given(message.getRetryCount()).willReturn(RETRY_COUNT);

        underTest.processMessage(message);

        then(message).should().setStatus(MessageStatus.PROCESSING_ERROR);
        then(rabbitTemplate).should().convertAndSend(EXCHANGE_ELITE_BASE_BACKFILL, MESSAGE_ELITE_BASE_BACKFILL, message);
    }

    @Test
    void retryForeignKeyConstraintValidation() {
        given(messageProcessor.canProcess(message)).willReturn(true);
        doThrow(new DataIntegrityViolationException("asd violates foreign key constraint asd"))
            .when(messageProcessor)
            .processMessage(message);
        given(properties.getMessageProcessorMaxRetryCount()).willReturn(RETRY_COUNT);
        given(properties.getMessageProcessorRetryDelay()).willReturn(RETRY_DELAY);
        given(message.getRetryCount()).willReturn(RETRY_COUNT - 2);
        given(message.getCreatedAt()).willReturn(CREATED_AT);

        underTest.processMessage(message);

        then(message).should().setStatus(MessageStatus.ARRIVED);
        then(message).should().setRetryCount(RETRY_COUNT - 1);
        then(message).should().setCreatedAt(CREATED_AT.plus(RETRY_DELAY));
        then(rabbitTemplate).should().convertAndSend(EXCHANGE_ELITE_BASE_BACKFILL, MESSAGE_ELITE_BASE_BACKFILL, message);
    }

    @Test
    void handleOtherDataIntegrityViolationException() {
        given(messageProcessor.canProcess(message)).willReturn(true);
        doThrow(new DataIntegrityViolationException("asd"))
            .when(messageProcessor)
            .processMessage(message);
        given(idGenerator.randomUuid()).willReturn(EXCEPTION_ID);

        underTest.processMessage(message);

        then(message).should().setStatus(MessageStatus.ERROR);
        then(message).should().setExceptionId(EXCEPTION_ID);
        then(errorReporterService).should().report(any(), any(DataIntegrityViolationException.class));
        then(rabbitTemplate).should().convertAndSend(EXCHANGE_ELITE_BASE_BACKFILL, MESSAGE_ELITE_BASE_BACKFILL, message);
    }

    @Test
    void handleException() {
        given(messageProcessor.canProcess(message)).willReturn(true);
        doThrow(new RuntimeException("asd"))
            .when(messageProcessor)
            .processMessage(message);
        given(idGenerator.randomUuid()).willReturn(EXCEPTION_ID);

        underTest.processMessage(message);

        then(message).should().setStatus(MessageStatus.ERROR);
        then(message).should().setExceptionId(EXCEPTION_ID);
        then(errorReporterService).should().report(any(), any(RuntimeException.class));
        then(rabbitTemplate).should().convertAndSend(EXCHANGE_ELITE_BASE_BACKFILL, MESSAGE_ELITE_BASE_BACKFILL, message);
    }
}