package com.github.saphyra.apphub.service.elite_base.common;

import com.github.saphyra.apphub.api.elite_base.server.EliteBaseEventController;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.elite_base.config.EliteBaseProperties;
import com.github.saphyra.apphub.service.elite_base.message_handling.dao.MessageDao;
import com.github.saphyra.apphub.service.elite_base.message_processing.processor.EdMessageProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@Slf4j
@RequiredArgsConstructor
//TODO unit test
class EliteBaseEventControllerImpl implements EliteBaseEventController {
    private final MessageDao messageDao;
    private final DateTimeUtil dateTimeUtil;
    private final EliteBaseProperties properties;
    private final EdMessageProcessor edMessageProcessor;

    @Override
    public void processMessages() {
        log.info("processMessages event arrived");
        edMessageProcessor.processMessages();
    }

    @Override
    public void resetUnhandled() {
        log.info("resetUnhandled event arrived");
        messageDao.resetUnhandled();
    }

    @Override
    public void deleteExpiredMessages() {
        log.info("deleteExpiredMessages event arrived");
        LocalDateTime expiration = dateTimeUtil.getCurrentDateTime()
            .minus(properties.getMessageExpiration());
        messageDao.deleteExpired(expiration);
    }
}
