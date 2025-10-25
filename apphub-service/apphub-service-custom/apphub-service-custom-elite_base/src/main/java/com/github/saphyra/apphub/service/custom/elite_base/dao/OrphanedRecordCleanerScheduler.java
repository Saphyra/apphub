package com.github.saphyra.apphub.service.custom.elite_base.dao;

import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.custom.elite_base.common.BufferSynchronizationService;
import com.github.saphyra.apphub.service.custom.elite_base.common.EliteBaseProperties;
import com.github.saphyra.apphub.service.custom.elite_base.common.MessageProcessingLock;
import com.github.saphyra.apphub.service.custom.elite_base.common.OrphanedRecordCleanerProperties;
import com.google.common.base.Stopwatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrphanedRecordCleanerScheduler {
    private final EliteBaseProperties properties;
    private final ExecutorServiceBean executorServiceBean;
    private final List<OrphanedRecordCleaner> orphanedRecordCleaners;
    private final ErrorReporterService errorReporterService;
    private final MessageProcessingLock messageProcessingLock;
    private final BufferSynchronizationService bufferSynchronizationService;
    private final SleepService sleepService;

    public synchronized void cleanup() {
        Lock writeLock = messageProcessingLock.writeLock();
        try {
            writeLock.lock();
            bufferSynchronizationService.synchronizeAll();

            OrphanedRecordCleanerProperties orphanedRecordCleanerProperties = properties.getOrphanedRecordCleaner();

            Semaphore semaphore = new Semaphore(orphanedRecordCleanerProperties.getProcessorParallelism());
            Set<Orphanage> finished = new CopyOnWriteArraySet<>();
            Set<Orphanage> started = new HashSet<>();
            Set<Orphanage> all = orphanedRecordCleaners.stream()
                .map(OrphanedRecordCleaner::getOrphanage)
                .collect(Collectors.toSet());

            AtomicInteger rowsDeleted = new AtomicInteger(0);
            Stopwatch stopwatch = Stopwatch.createStarted();

            while (started.size() < all.size() && stopwatch.elapsed(TimeUnit.SECONDS) < orphanedRecordCleanerProperties.getTimeout().toSeconds()) {
                all.stream()
                    .filter(orphanage -> !started.contains(orphanage))
                    .forEach(orphanage -> {
                        if (schedule(orphanage, finished, semaphore, rowsDeleted)) {
                            started.add(orphanage);
                        }
                    });

                Set<Orphanage> inProgress = started.stream()
                    .filter(orphanage -> !finished.contains(orphanage))
                    .collect(Collectors.toSet());
                Set<Orphanage> toBeStarted = all.stream()
                    .filter(orphanage -> !started.contains(orphanage))
                    .collect(Collectors.toSet());
                log.info("{}/{}/{} Finished orphanedRecordCleaners: {} In progress: {} To be started: {}", finished.size(), inProgress.size(), all.size(), finished, inProgress, toBeStarted);

                sleepService.sleep(2000);
            }

            log.info("All orphaned record cleaners have been started. Waiting for their completion...");

            while (finished.size() < all.size() && stopwatch.elapsed(TimeUnit.SECONDS) < orphanedRecordCleanerProperties.getTimeout().toSeconds()) {
                List<Orphanage> inProgress = all.stream()
                    .filter(orphanage -> !finished.contains(orphanage))
                    .toList();
                log.info("{}/{} Finished orphanedRecordCleaners: {} In progress: {}", finished.size(), all.size(), finished, inProgress);

                sleepService.sleep(4000);
            }
            stopwatch.stop();
            log.info("Orphaned record cleanup finished. {} rows were deleted.", rowsDeleted);
            errorReporterService.report("EliteBase orphanedRecordCleanup finished in %s seconds. %s rows were deleted.".formatted(stopwatch.elapsed(TimeUnit.SECONDS), rowsDeleted));
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * @return true if scheduled, false if preconditions are not met
     */
    private boolean schedule(Orphanage orphanage, Set<Orphanage> finished, Semaphore semaphore, AtomicInteger rowsDeleted) {
        OrphanedRecordCleaner orphanedRecordCleaner = orphanedRecordCleaners.stream()
            .filter(cleaner -> cleaner.getOrphanage() == orphanage)
            .findAny()
            .orElseThrow(() -> new IllegalStateException("OrphanedRecordCleaner not found for orphanage " + orphanage));

        if (!finished.containsAll(orphanedRecordCleaner.getPreconditions())) {
            List<Orphanage> waitingFor = orphanedRecordCleaner.getPreconditions()
                .stream()
                .filter(precondition -> !finished.contains(precondition))
                .toList();

            log.info("Preconditions are not met for {}. Preconditions: {}, waiting for: {}", orphanage, orphanedRecordCleaner.getPreconditions(), waitingFor);
            return false;
        }

        executorServiceBean.execute(() -> {
            semaphore.acquireUninterruptibly();

            int rows = orphanedRecordCleaner.cleanupOrphanedRecords();

            rowsDeleted.addAndGet(rows);
            finished.add(orphanage);

            semaphore.release();
        });

        return true;
    }
}
