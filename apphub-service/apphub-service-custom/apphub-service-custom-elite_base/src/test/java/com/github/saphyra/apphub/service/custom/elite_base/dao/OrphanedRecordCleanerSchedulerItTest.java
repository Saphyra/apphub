package com.github.saphyra.apphub.service.custom.elite_base.dao;

import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.custom.elite_base.common.BufferSynchronizationService;
import com.github.saphyra.apphub.service.custom.elite_base.common.MessageProcessingLock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class OrphanedRecordCleanerSchedulerItTest {
    @Autowired
    private OrphanedRecordCleanerScheduler underTest;

    @MockBean
    private ErrorReporterService errorReporterService;

    @Autowired
    private List<OrphanedRecordCleaner> orphanedRecordCleaners;

    @MockBean
    private MessageProcessingLock messageProcessingLock;

    @MockBean
    private BufferSynchronizationService bufferSynchronizationService;

    @Mock
    private ReentrantReadWriteLock.WriteLock writeLock;

    @Test
    @Timeout(30)
    void cleanup() {
        given(messageProcessingLock.writeLock()).willReturn(writeLock);

        underTest.cleanup();

        then(bufferSynchronizationService).should().synchronizeAll();
        then(errorReporterService).should(times(orphanedRecordCleaners.size() + 1)).report(any());
        then(errorReporterService).should(times(0)).report(any(), any());
        then(writeLock).should().lock();
        then(writeLock).should().unlock();
    }
}