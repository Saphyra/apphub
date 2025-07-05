package com.github.saphyra.apphub.service.custom.elite_base.common;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.custom.elite_base.dao.OrphanedRecordCleaner;
import com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao.MessageDao;
import com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao.MessageStatus;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.processor.EdMessageProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class EliteBaseEventControllerImplTest {
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();
    private static final Duration PROCESSED_MESSAGE_EXPIRATION = Duration.of(3, ChronoUnit.SECONDS);
    private static final Duration MESSAGE_EXPIRATION = Duration.of(43, ChronoUnit.SECONDS);
    private static final int NUMBER_OF_DELETED_RECORDS = 23;

    @Mock
    private MessageDao messageDao;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private EliteBaseProperties properties;

    @Mock
    private EdMessageProcessor edMessageProcessor;

    @Mock
    private ExecutorServiceBean executorServiceBean;

    @Mock
    private OrphanedRecordCleaner orphanedRecordCleaner;

    @Mock
    private ErrorReporterService errorReporterService;

    private final MessageProcessingLock messageProcessingLock = new MessageProcessingLock();

    @Mock
    private BufferSynchronizationService bufferSynchronizationService;

    private EliteBaseEventControllerImpl underTest;

    @BeforeEach
    void setUp() {
        underTest = EliteBaseEventControllerImpl.builder()
            .messageDao(messageDao)
            .dateTimeUtil(dateTimeUtil)
            .properties(properties)
            .edMessageProcessor(edMessageProcessor)
            .executorServiceBean(executorServiceBean)
            .orphanedRecordCleaners(List.of(orphanedRecordCleaner))
            .errorReporterService(errorReporterService)
            .messageProcessingLock(messageProcessingLock)
            .bufferSynchronizationService(bufferSynchronizationService)
            .build();
    }

    @Test
    void processMessages() {
        given(executorServiceBean.execute(any())).willAnswer(invocation -> {
            invocation.getArgument(0, Runnable.class).run();
            return null;
        });

        underTest.processMessages();

        then(edMessageProcessor).should().processMessages();
    }

    @Test
    void resetUnhandled() {
        underTest.resetUnhandled();

        then(messageDao).should().resetUnhandled();
    }

    @Test
    void deleteExpiredMessages() {
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);
        given(properties.getProcessedMessageExpiration()).willReturn(PROCESSED_MESSAGE_EXPIRATION);
        given(properties.getMessageExpiration()).willReturn(MESSAGE_EXPIRATION);

        underTest.deleteExpiredMessages();

        then(messageDao).should().deleteExpired(CURRENT_TIME.minus(PROCESSED_MESSAGE_EXPIRATION), List.of(MessageStatus.PROCESSED));
        then(messageDao).should().deleteExpired(CURRENT_TIME.minus(MESSAGE_EXPIRATION), Arrays.stream(MessageStatus.values()).filter(messageStatus -> messageStatus != MessageStatus.ARRIVED).toList());
    }

    @Test
    void resetError() {
        underTest.resetError();

        then(messageDao).should().resetError();
    }

    @Test
    void cleanupOrphanedRecords() {
        given(executorServiceBean.execute(any())).willAnswer(invocationOnMock -> {
            invocationOnMock.getArgument(0, Runnable.class).run();
            return null;
        });

        given(properties.getOrphanedRecordProcessorParallelism()).willReturn(1);

        given(executorServiceBean.asyncProcess(any())).willAnswer(invocationOnMock -> CompletableFuture.completedFuture(ExecutionResult.success(invocationOnMock.getArgument(0, Callable.class).call())));
        given(orphanedRecordCleaner.cleanupOrphanedRecords()).willReturn(NUMBER_OF_DELETED_RECORDS);

        underTest.cleanupOrphanedRecords();

        then(bufferSynchronizationService).should().synchronizeAll();
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        then(errorReporterService).should().report(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).contains(String.valueOf(NUMBER_OF_DELETED_RECORDS));
    }
}