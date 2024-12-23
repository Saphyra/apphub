package com.github.saphyra.service.elite_base.message_handling.dao;

import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import org.springframework.stereotype.Component;

@Component
public class MessageDao extends AbstractDao<MessageEntity, EdMessage, String, MessageRepository> {
    MessageDao(MessageConverter converter, MessageRepository repository) {
        super(converter, repository);
    }
}
