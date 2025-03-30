package com.github.saphyra.apphub.service.custom.elite_base.message_processing.processor;

import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopic;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.concurrency.FixedExecutorServiceBean;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.performance_reporting.PerformanceReporter;
import com.github.saphyra.apphub.service.custom.elite_base.common.PerformanceReportingKey;
import com.github.saphyra.apphub.service.custom.elite_base.common.EliteBaseProperties;
import com.github.saphyra.apphub.service.custom.elite_base.common.MessageProcessingDelayedException;
import com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao.MessageDao;
import com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao.MessageStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class EdMessageProcessorTest {
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();
    private static final Integer MESSAGE_PROCESSOR_BATCH_SIZE = 3;
    private static final Integer MESSAGE_PROCESSOR_SUBLIST_SIZE = 2;
    private static final UUID EXCEPTION_ID = UUID.randomUUID();
    private static final Integer MAX_RETRY_COUNT = 234;
    private static final Integer RETRY_COUNT = 23;
    private static final Duration RETRY_DELAY = Duration.of(23, ChronoUnit.SECONDS);
    private static final String SCHEMA_REF = "schema-ref";

    @Mock
    private EliteBaseProperties properties;

    @Mock
    private MessageDao messageDao;

    @Mock
    private FixedExecutorServiceBean executorServiceBean;

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private ErrorReporterService errorReporterService;

    @Mock
    private MessageProcessor messageProcessor;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private PerformanceReporter performanceReporter;

    private EdMessageProcessor underTest;

    @Mock
    private EdMessage edMessage;

    @Mock
    private EdMessage unhandledMessage;

    @Mock
    private Future<ExecutionResult<Void>> future;

    @BeforeEach
    void setUp() {
        underTest = EdMessageProcessor.builder()
            .properties(properties)
            .messageDao(messageDao)
            .executorServiceBean(executorServiceBean)
            .idGenerator(idGenerator)
            .errorReporterService(errorReporterService)
            .messageProcessors(List.of(messageProcessor))
            .dateTimeUtil(dateTimeUtil)
            .performanceReporter(performanceReporter)
            .build();

        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);
        given(properties.getMessageProcessorBatchSize()).willReturn(MESSAGE_PROCESSOR_BATCH_SIZE);
        given(properties.getMessageProcessorSublistSize()).willReturn(MESSAGE_PROCESSOR_SUBLIST_SIZE);
        given(executorServiceBean.execute(any())).willAnswer(invocation -> {
            invocation.getArgument(0, Runnable.class).run();
            return future;
        });
        doAnswer(invocation -> {
            invocation.getArgument(0, Runnable.class).run();
            return null;
        }).when(performanceReporter).wrap(any(Runnable.class), any(PerformanceReportingTopic.class), anyString());
        given(performanceReporter.wrap(any(Callable.class), any(PerformanceReportingTopic.class), anyString())).willAnswer(invocation -> {
            return invocation.getArgument(0, Callable.class).call();
        });
    }

    @AfterEach
    void verify() throws ExecutionException, InterruptedException {
        then(future).should(atLeast(1)).get();
        then(performanceReporter).should().wrap(any(Callable.class), eq(PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING), ArgumentMatchers.eq(PerformanceReportingKey.QUERY_ARRIVED_MESSAGES.name()));
    }

    @Test
    void error() {
        given(messageDao.getMessages(CURRENT_TIME, MESSAGE_PROCESSOR_BATCH_SIZE)).willReturn(List.of(edMessage));
        given(messageProcessor.canProcess(edMessage)).willThrow(new RuntimeException());
        given(idGenerator.randomUuid()).willReturn(EXCEPTION_ID);

        underTest.processMessages();

        then(edMessage).should().setStatus(MessageStatus.ERROR);
        then(edMessage).should().setExceptionId(EXCEPTION_ID);
        then(messageDao).should().save(edMessage);
        then(errorReporterService).should().report(any(), any());
    }

    @Test
    void delayedException_limitReached() {
        given(messageDao.getMessages(CURRENT_TIME, MESSAGE_PROCESSOR_BATCH_SIZE)).willReturn(List.of(edMessage));
        given(messageProcessor.canProcess(edMessage)).willThrow(new MessageProcessingDelayedException("asd"));
        given(idGenerator.randomUuid()).willReturn(EXCEPTION_ID);
        given(properties.getMessageProcessorMaxRetryCount()).willReturn(MAX_RETRY_COUNT);
        given(edMessage.getRetryCount()).willReturn(MAX_RETRY_COUNT);

        underTest.processMessages();

        then(edMessage).should().setStatus(MessageStatus.PROCESSING_ERROR);
        then(edMessage).should().setExceptionId(EXCEPTION_ID);
        then(messageDao).should().save(edMessage);
        then(errorReporterService).shouldHaveNoInteractions();
    }

    @Test
    void delayedException() {
        given(messageDao.getMessages(CURRENT_TIME, MESSAGE_PROCESSOR_BATCH_SIZE)).willReturn(List.of(edMessage));
        given(messageProcessor.canProcess(edMessage)).willThrow(new MessageProcessingDelayedException("asd"));
        given(properties.getMessageProcessorMaxRetryCount()).willReturn(MAX_RETRY_COUNT);
        given(edMessage.getRetryCount()).willReturn(RETRY_COUNT);
        given(properties.getMessageProcessorRetryDelay()).willReturn(RETRY_DELAY);
        given(edMessage.getCreatedAt()).willReturn(CURRENT_TIME);
        given(edMessage.getSchemaRef()).willReturn(SCHEMA_REF);

        underTest.processMessages();

        then(edMessage).should().setStatus(MessageStatus.ARRIVED);
        then(edMessage).should().setRetryCount(RETRY_COUNT + 1);
        then(edMessage).should().setCreatedAt(CURRENT_TIME.plus(RETRY_DELAY));
        then(messageDao).should().save(edMessage);
        then(errorReporterService).shouldHaveNoInteractions();
        then(performanceReporter).should().wrap(any(Runnable.class), eq(PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING), eq(PerformanceReportingKey.PROCESS_MESSAGE.formatted(SCHEMA_REF)));
    }

    @Test
    void processMessages() {
        given(messageDao.getMessages(CURRENT_TIME, MESSAGE_PROCESSOR_BATCH_SIZE)).willReturn(List.of(edMessage, unhandledMessage, edMessage));
        given(messageProcessor.canProcess(edMessage)).willReturn(true);
        given(messageProcessor.canProcess(unhandledMessage)).willReturn(false);
        given(edMessage.getSchemaRef()).willReturn(SCHEMA_REF);
        given(unhandledMessage.getSchemaRef()).willReturn("asd");

        underTest.processMessages();

        then(executorServiceBean).should(times(2)).execute(any());
        then(unhandledMessage).should().setStatus(MessageStatus.UNHANDLED);
        then(messageDao).should().save(unhandledMessage);
        then(edMessage).should(times(2)).setStatus(MessageStatus.PROCESSED);
        then(messageDao).should(times(2)).save(edMessage);
        then(messageProcessor).should(times(2)).processMessage(edMessage);
        then(performanceReporter).should(times(2)).wrap(any(Runnable.class), eq(PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING), eq(PerformanceReportingKey.PROCESS_MESSAGE.formatted(SCHEMA_REF)));
    }
}