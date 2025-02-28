package com.github.saphyra.apphub.service.custom.elite_base.common;

import com.github.saphyra.apphub.api.elite_base.server.EliteBaseEventController;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao.MessageDao;
import com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao.MessageStatus;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.processor.EdMessageProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
class EliteBaseEventControllerImpl implements EliteBaseEventController {
    private final MessageDao messageDao;
    private final DateTimeUtil dateTimeUtil;
    private final EliteBaseProperties properties;
    private final EdMessageProcessor edMessageProcessor;
    private final ExecutorServiceBean executorServiceBean;

    @Override
    public void processMessages() {
        log.info("processMessages event arrived");
        executorServiceBean.execute(edMessageProcessor::processMessages);
    }

    @Override
    public void resetUnhandled() {
        log.info("resetUnhandled event arrived");
        messageDao.resetUnhandled();
    }

    @Override
    public void deleteExpiredMessages() {
        log.info("deleteExpiredMessages event arrived");
        LocalDateTime processedExpiration = dateTimeUtil.getCurrentDateTime()
            .minus(properties.getProcessedMessageExpiration());

        messageDao.deleteExpired(processedExpiration, List.of(MessageStatus.PROCESSED));

        LocalDateTime expiration = dateTimeUtil.getCurrentDateTime()
            .minus(properties.getMessageExpiration());
        messageDao.deleteExpired(expiration, Arrays.asList(MessageStatus.values()));
    }

    @Override
    public void resetError() {
        log.info("resetError event arrived");
        messageDao.resetError();
    }
}
