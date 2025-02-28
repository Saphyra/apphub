package com.github.saphyra.apphub.service.custom.elite_base.common;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao.MessageDao;
import com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao.MessageStatus;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.processor.EdMessageProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class EliteBaseEventControllerImplTest {
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();
    private static final Duration PROCESSED_MESSAGE_EXPIRATION = Duration.of(3, ChronoUnit.SECONDS);
    private static final Duration MESSAGE_EXPIRATION = Duration.of(43, ChronoUnit.SECONDS);

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

    @InjectMocks
    private EliteBaseEventControllerImpl underTest;

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
        then(messageDao).should().deleteExpired(CURRENT_TIME.minus(MESSAGE_EXPIRATION), Arrays.asList(MessageStatus.values()));
    }

    @Test
    void resetError() {
        underTest.resetError();

        then(messageDao).should().resetError();
    }
}