package com.github.saphyra.apphub.service.feature.elite_base.processor.db_writer.controller;

import com.github.saphyra.apphub.api.feature.elite_base.message.MessageStatus;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.feature.elite_base.processor.db_writer.config.EliteBaseDbCacheProperties;
import com.github.saphyra.apphub.service.feature.elite_base.processor.db_writer.dao.MessageDao;
import com.github.saphyra.apphub.service.feature.elite_base.processor.db_writer.service.MessageProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class EliteBaseDbCacheEventControllerImplTest {
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();
    private static final Duration PROCESSED_EXPIRATION = Duration.ofDays(7);
    private static final Duration MESSAGE_EXPIRATION = Duration.ofDays(30);

    @Mock
    private  MessageDao messageDao;

    @Mock
    private  DateTimeUtil dateTimeUtil;

    @Mock
    private  EliteBaseDbCacheProperties properties;

    @Mock
    private  MessageProcessor messageProcessor;

    @InjectMocks
    private EliteBaseDbCacheEventControllerImpl underTest;

    @Test
    void processMessages() {
        underTest.processMessages();

        then(messageProcessor).should().processNextBatch();
    }

    @Test
    void resetUnhandled() {
        underTest.resetUnhandled();

        then(messageDao).should().resetUnhandled();
    }

    @Test
    void deleteExpiredMessages() {
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);
        given(properties.getProcessedMessageExpiration()).willReturn(PROCESSED_EXPIRATION);
        given(properties.getMessageExpiration()).willReturn(MESSAGE_EXPIRATION);

        underTest.deleteExpiredMessages();

        then(messageDao).should().deleteExpired(CURRENT_TIME.minus(PROCESSED_EXPIRATION), List.of(MessageStatus.PROCESSED));
        then(messageDao).should().deleteExpired(CURRENT_TIME.minus(MESSAGE_EXPIRATION), List.of(MessageStatus.UNHANDLED, MessageStatus.PROCESSED, MessageStatus.ERROR, MessageStatus.PROCESSING_ERROR));
    }

    @Test
    void resetError() {
        underTest.resetError();

        then(messageDao).should().resetError();
    }
}