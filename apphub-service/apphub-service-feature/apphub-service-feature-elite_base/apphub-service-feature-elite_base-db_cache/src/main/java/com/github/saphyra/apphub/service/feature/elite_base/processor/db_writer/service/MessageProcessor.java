package com.github.saphyra.apphub.service.feature.elite_base.processor.db_writer.service;

import com.github.saphyra.apphub.api.feature.elite_base.message.EdMessage;
import com.github.saphyra.apphub.api.feature.elite_base.message.MessageStatus;
import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.monitoring.instrument.MonitoringInstruments;
import com.github.saphyra.apphub.service.feature.elite_base.processor.db_writer.common.PerformanceReportingKey;
import com.github.saphyra.apphub.service.feature.elite_base.processor.db_writer.config.EliteBaseDbCacheProperties;
import com.github.saphyra.apphub.service.feature.elite_base.processor.db_writer.dao.MessageDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static com.github.saphyra.apphub.lib.config.rabbitmq.RabbitMqConstants.EXCHANGE_ELITE_BASE_STORED;
import static com.github.saphyra.apphub.lib.config.rabbitmq.RabbitMqConstants.MESSAGE_ELITE_BASE_STORED;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageProcessor {
    private final MonitoringInstruments monitoringInstruments;
    private final DateTimeUtil dateTimeUtil;
    private final EliteBaseDbCacheProperties properties;
    private final MessageDao messageDao;
    private final RabbitTemplate rabbitTemplate;
    private final EdMessageLock edMessageLock;

    public void processNextBatch() {
        List<EdMessage> messages = Collections.emptyList();
        try {
            edMessageLock.getReadWriteLock()
                .writeLock()
                .lock();

            messages = monitoringInstruments.wrap(
                () -> messageDao.getMessages(dateTimeUtil.getCurrentDateTime(), properties.getMessageProcessorBatchSize()),
                Feature.ELITE_BASE_MESSAGE_PROCESSING,
                PerformanceReportingKey.QUERY_ARRIVED_MESSAGES.name()
            );
            if (messages.isEmpty()) {
                return;
            }

            messages.forEach(edMessage -> {
                rabbitTemplate.convertAndSend(EXCHANGE_ELITE_BASE_STORED, MESSAGE_ELITE_BASE_STORED, edMessage);
                edMessage.setStatus(MessageStatus.PROCESSED);
            });

            messageDao.saveAll(messages);

            log.info("{} messages processed.", messages.size());
        } catch (Exception e) {
            log.warn("Failed batch-saving messages. Trying them one-by-one");

            messages.forEach(messageDao::save);
        } finally {
            edMessageLock.getReadWriteLock()
                .writeLock()
                .unlock();
        }
    }
}
