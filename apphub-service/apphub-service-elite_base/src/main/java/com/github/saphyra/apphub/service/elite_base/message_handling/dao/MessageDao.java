package com.github.saphyra.apphub.service.elite_base.message_handling.dao;

import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.DateTimeConverter;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class MessageDao extends AbstractDao<MessageEntity, EdMessage, String, MessageRepository> {
    private final DateTimeConverter dateTimeConverter;

    MessageDao(MessageConverter converter, MessageRepository repository, DateTimeConverter dateTimeConverter) {
        super(converter, repository);
        this.dateTimeConverter = dateTimeConverter;
    }

    public void resetUnhandled() {
        repository.resetUnhandled(MessageStatus.UNHANDLED, MessageStatus.ARRIVED);
    }

    public void deleteExpired(LocalDateTime expiration, List<MessageStatus> statuses) {
        repository.deleteByCreatedAtBeforeAndStatusIn(dateTimeConverter.convertDomain(expiration), statuses);
    }

    public List<EdMessage> getMessages(LocalDateTime timeLimit, Integer messageProcessorBatchSize) {
        return converter.convertEntity(repository.getByCreatedAtBeforeAndStatusOrderByCreatedAtAsc(dateTimeConverter.convertDomain(timeLimit), MessageStatus.ARRIVED, PageRequest.of(0, messageProcessorBatchSize)));
    }
}
