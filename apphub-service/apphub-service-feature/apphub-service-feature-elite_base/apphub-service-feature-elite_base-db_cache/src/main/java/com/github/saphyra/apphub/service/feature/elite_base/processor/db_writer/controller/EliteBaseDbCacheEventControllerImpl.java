package com.github.saphyra.apphub.service.feature.elite_base.processor.db_writer.controller;

import com.github.saphyra.apphub.api.feature.elite_base.db_cache.EliteBaseDbCacheEventController;
import com.github.saphyra.apphub.api.feature.elite_base.message.MessageStatus;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.feature.elite_base.processor.db_writer.config.EliteBaseDbCacheProperties;
import com.github.saphyra.apphub.service.feature.elite_base.processor.db_writer.dao.MessageDao;
import com.github.saphyra.apphub.service.feature.elite_base.processor.db_writer.service.MessageProcessor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Builder
class EliteBaseDbCacheEventControllerImpl implements EliteBaseDbCacheEventController {
    private final MessageDao messageDao;
    private final DateTimeUtil dateTimeUtil;
    private final EliteBaseDbCacheProperties properties;
    private final MessageProcessor messageProcessor;

    @Override
    public void processMessages() {
        log.info("processMessages event arrived");
        messageProcessor.processNextBatch();
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
        messageDao.deleteExpired(expiration, Arrays.stream(MessageStatus.values()).filter(messageStatus -> messageStatus != MessageStatus.ARRIVED).toList());
    }

    @Override
    public void resetError() {
        log.info("resetError event arrived");
        messageDao.resetError();
    }
}
