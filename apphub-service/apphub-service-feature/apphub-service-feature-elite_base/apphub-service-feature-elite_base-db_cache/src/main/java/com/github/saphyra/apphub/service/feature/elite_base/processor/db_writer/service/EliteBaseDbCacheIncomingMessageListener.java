package com.github.saphyra.apphub.service.feature.elite_base.processor.db_writer.service;

import com.github.saphyra.apphub.api.feature.elite_base.message.EdMessage;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.feature.elite_base.processor.db_writer.dao.MessageDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import static com.github.saphyra.apphub.lib.config.rabbitmq.RabbitMqConstants.MESSAGE_LISTENER_ELITE_BASE_INCOMING;

@Component(MESSAGE_LISTENER_ELITE_BASE_INCOMING)
@Slf4j
@RequiredArgsConstructor
class EliteBaseDbCacheIncomingMessageListener implements MessageListener {
    private final MessageDao messageDao;
    private final ObjectMapper objectMapper;
    private final ErrorReporterService errorReporterService;
    private final EdMessageLock edMessageLock;

    @Override
    public void onMessage(Message message) {
        try {
            edMessageLock.getReadWriteLock()
                .readLock()
                .lock();

            EdMessage edMessage = objectMapper.readValue(message.getBody(), EdMessage.class);
            log.debug("Received message: {}", edMessage);
            messageDao.save(edMessage);
        } catch (Exception e) {
            errorReporterService.report("Failed to process incoming message", e);
        } finally {
            edMessageLock.getReadWriteLock()
                .readLock()
                .unlock();
        }
    }
}
