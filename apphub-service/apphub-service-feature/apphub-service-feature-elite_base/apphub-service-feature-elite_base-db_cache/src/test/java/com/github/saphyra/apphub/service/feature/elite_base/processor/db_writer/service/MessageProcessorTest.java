package com.github.saphyra.apphub.service.feature.elite_base.processor.db_writer.service;

import com.github.saphyra.apphub.api.feature.elite_base.message.EdMessage;
import com.github.saphyra.apphub.api.feature.elite_base.message.MessageStatus;
import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.monitoring.instrument.MonitoringInstruments;
import com.github.saphyra.apphub.service.feature.elite_base.processor.db_writer.common.PerformanceReportingKey;
import com.github.saphyra.apphub.service.feature.elite_base.processor.db_writer.config.EliteBaseDbCacheProperties;
import com.github.saphyra.apphub.service.feature.elite_base.processor.db_writer.dao.MessageDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

import static com.github.saphyra.apphub.lib.config.rabbitmq.RabbitMqConstants.EXCHANGE_ELITE_BASE_STORED;
import static com.github.saphyra.apphub.lib.config.rabbitmq.RabbitMqConstants.MESSAGE_ELITE_BASE_STORED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
class MessageProcessorTest {
    private static final LocalDateTime CURRENT_DATE = LocalDateTime.now();
    private static final Integer BATCH_SIZE = 32;

    @Mock
    private MonitoringInstruments monitoringInstruments;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private EliteBaseDbCacheProperties properties;

    @Mock
    private MessageDao messageDao;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Spy
    private final EdMessageLock edMessageLock = new EdMessageLock();

    @InjectMocks
    private MessageProcessor underTest;

    @Mock
    private EdMessage edMessage;

    @Test
    void processNextBatch() {
        doAnswer(invocation -> {
            Supplier<?> runnable = invocation.getArgument(0);
            return runnable.get();
        }).when(monitoringInstruments).wrap(any(Supplier.class), eq(Feature.ELITE_BASE_MESSAGE_PROCESSING), eq(PerformanceReportingKey.QUERY_ARRIVED_MESSAGES.name()));

        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_DATE);
        given(properties.getMessageProcessorBatchSize()).willReturn(BATCH_SIZE);
        given(messageDao.getMessages(CURRENT_DATE, BATCH_SIZE)).willReturn(List.of(edMessage));

        underTest.processNextBatch();

        then(rabbitTemplate).should().convertAndSend(EXCHANGE_ELITE_BASE_STORED, MESSAGE_ELITE_BASE_STORED, edMessage);
        then(edMessage).should().setStatus(MessageStatus.PROCESSED);
        then(messageDao).should().saveAll(List.of(edMessage));
    }
}