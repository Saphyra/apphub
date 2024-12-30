package com.github.saphyra.apphub.service.elite_base.message_handling.dao;

import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
//TODO unit test
public class MessageDao extends AbstractDao<MessageEntity, EdMessage, String, MessageRepository> {
    MessageDao(MessageConverter converter, MessageRepository repository) {
        super(converter, repository);
    }

    public void resetUnhandled() {
        repository.resetUnhandled(MessageStatus.UNHANDLED, MessageStatus.ARRIVED);
    }

    public void deleteExpired(LocalDateTime expiration) {
        repository.deleteByCreatedAtBefore(expiration.toString());
    }

    public List<EdMessage> getMessages(Integer messageProcessorBatchSize) {
        return converter.convertEntity(repository.findByStatusOrderByCreatedAtAsc(MessageStatus.ARRIVED, PageRequest.of(0, messageProcessorBatchSize)));
    }
}
