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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class EdMessageHandlerHelperTest {
    private static final String OUTPUT_STRING = "output-string";
    private static final LocalDateTime LAST_MESSAGE = LocalDateTime.now();
    private static final Duration INCOMING_MESSAGE_TIMEOUT = Duration.ofMinutes(2);

    @Mock
    private MessageFactory messageFactory;

    @Mock
    private EdMessageProcessor edMessageProcessor;

    @Mock
    private MessageProcessingLock messageProcessingLock;

    @Mock
    private MessageDao messageDao;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private EliteBaseProperties eliteBaseProperties;

    @Mock
    private ErrorReporterService errorReporterService;

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @Mock
    private ExecutorServiceBean executorServiceBean;

    @Mock
    private BufferSynchronizationService bufferSynchronizationService;

    @InjectMocks
    private EdMessageHandlerHelper underTest;

    @Mock
    private EdMessage message;

    @Mock
    private ReentrantReadWriteLock.ReadLock readLock;

    @Mock
    private ReentrantReadWriteLock.WriteLock writeLock;

    @Mock
    private ConfigurableApplicationContext applicationContext;

    @Test
    void handleMessage_lockAvailable() {
        given(executorServiceBean.execute(any())).willAnswer(invocation -> {
            invocation.getArgument(0, Runnable.class).run();
            return null;
        });
        given(messageFactory.create(OUTPUT_STRING)).willReturn(message);
        given(messageProcessingLock.readLock()).willReturn(readLock);
        given(readLock.tryLock()).willReturn(true);

        underTest.handleMessage(OUTPUT_STRING);

        then(edMessageProcessor).should().processMessage(message);
        then(readLock).should().unlock();
        then(messageDao).shouldHaveNoInteractions();
    }

    @Test
    void handleMessage_lockLocked() {
        given(executorServiceBean.execute(any())).willAnswer(invocation -> {
            invocation.getArgument(0, Runnable.class).run();
            return null;
        });
        given(messageFactory.create(OUTPUT_STRING)).willReturn(message);
        given(messageProcessingLock.readLock()).willReturn(readLock);
        given(readLock.tryLock()).willReturn(false);

        underTest.handleMessage(OUTPUT_STRING);

        then(edMessageProcessor).shouldHaveNoInteractions();
        then(messageDao).should().save(message);
    }

    @Test
    void shutdownNeeded() {
        given(dateTimeUtil.getCurrentDateTime()).willReturn(LAST_MESSAGE.plus(INCOMING_MESSAGE_TIMEOUT).plusSeconds(1));
        given(eliteBaseProperties.getIncomingMessageTimeout()).willReturn(INCOMING_MESSAGE_TIMEOUT);

        assertThat(underTest.shutdownNeeded(LAST_MESSAGE)).isTrue();

        then(errorReporterService).should().report(anyString());
    }

    @Test
    void shutdownIsNotNeeded() {
        given(dateTimeUtil.getCurrentDateTime()).willReturn(LAST_MESSAGE.plus(INCOMING_MESSAGE_TIMEOUT).minusSeconds(1));
        given(eliteBaseProperties.getIncomingMessageTimeout()).willReturn(INCOMING_MESSAGE_TIMEOUT);

        assertThat(underTest.shutdownNeeded(LAST_MESSAGE)).isFalse();

        then(errorReporterService).shouldHaveNoInteractions();
    }

    @Test
    void shutdown() {
        given(messageProcessingLock.writeLock()).willReturn(writeLock);
        given(applicationContextProxy.getContext()).willReturn(applicationContext);

        underTest.shutdown();

        applicationContext.stop();

        then(writeLock).should().lock();
        then(writeLock).should(times(0)).unlock();
        then(bufferSynchronizationService).should().synchronizeAll();
        then(applicationContext).should().registerShutdownHook();
        then(applicationContext).should().close();
    }
}