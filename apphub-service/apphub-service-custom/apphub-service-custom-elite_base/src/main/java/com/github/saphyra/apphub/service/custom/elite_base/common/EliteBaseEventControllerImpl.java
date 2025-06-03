package com.github.saphyra.apphub.service.custom.elite_base.common;

import com.github.saphyra.apphub.api.elite_base.server.EliteBaseEventController;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.custom.elite_base.dao.OrphanedRecordCleaner;
import com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao.MessageDao;
import com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao.MessageStatus;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.processor.EdMessageProcessor;
import com.google.common.base.Stopwatch;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequiredArgsConstructor
@Builder
class EliteBaseEventControllerImpl implements EliteBaseEventController {
    private final MessageDao messageDao;
    private final DateTimeUtil dateTimeUtil;
    private final EliteBaseProperties properties;
    private final EdMessageProcessor edMessageProcessor;
    private final ExecutorServiceBean executorServiceBean;
    private final List<OrphanedRecordCleaner> orphanedRecordCleaners;
    private final ErrorReporterService errorReporterService;

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
        messageDao.deleteExpired(expiration, Arrays.stream(MessageStatus.values()).filter(messageStatus -> messageStatus != MessageStatus.ARRIVED).toList());
    }

    @Override
    public void resetError() {
        log.info("resetError event arrived");
        messageDao.resetError();
    }

    @Override
    public void cleanupOrphanedRecords() {
        log.info("cleanupOrphanedRecords event arrived");

        Semaphore semaphore = new Semaphore(properties.getOrphanedRecordProcessorParallelism());

        executorServiceBean.execute(() -> {
            Stopwatch stopwatch = Stopwatch.createStarted();

            List<Future<ExecutionResult<Integer>>> progress = orphanedRecordCleaners.stream()
                .map(orphanedRecordCleaner -> executorServiceBean.asyncProcess(() -> {
                    try {
                        semaphore.acquire();
                        return orphanedRecordCleaner.cleanupOrphanedRecords();
                    } finally {
                        semaphore.release();
                    }
                }))
                .toList();

            int rowsDeleted = 0;

            for (Future<ExecutionResult<Integer>> future : progress) {
                try {
                    rowsDeleted += future.get()
                        .getOrThrow();
                } catch (Exception e) {
                    log.error("OrphanedRecord cleanup failed", e);
                    errorReporterService.report("Orphaned record cleanup failed", e);
                }
            }

            stopwatch.stop();
            log.info("Orphaned record cleanup finished. {} rows were deleted.", rowsDeleted);
            errorReporterService.report("EliteBase orphanedRecordCleanup finished in %s seconds. %s rows were deleted.".formatted(stopwatch.elapsed(TimeUnit.SECONDS), rowsDeleted));
        });
    }
}
