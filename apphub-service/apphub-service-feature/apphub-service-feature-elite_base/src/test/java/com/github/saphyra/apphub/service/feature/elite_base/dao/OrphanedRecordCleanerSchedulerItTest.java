package com.github.saphyra.apphub.service.feature.elite_base.dao;

import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.monitoring.core.MetricRegistry;
import com.github.saphyra.apphub.service.feature.elite_base.common.BufferSynchronizationService;
import com.github.saphyra.apphub.service.feature.elite_base.common.MessageProcessingLock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static org.assertj.core.api.Assertions.assertThat;
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

    @MockitoBean
    private ErrorReporterService errorReporterService;

    @Autowired
    private List<OrphanedRecordCleaner> orphanedRecordCleaners;

    @MockitoBean
    private MessageProcessingLock messageProcessingLock;

    @MockitoBean
    private BufferSynchronizationService bufferSynchronizationService;

    @Mock
    private ReentrantReadWriteLock.WriteLock writeLock;

    @Autowired
    private MetricRegistry metricRegistry;

    @Test
    @Timeout(30)
    void cleanup() {
        given(messageProcessingLock.writeLock()).willReturn(writeLock);

        underTest.cleanup();

        assertThat(metricRegistry.getRegistry().values().stream().flatMap(List::stream)).hasSize(orphanedRecordCleaners.size() + 1);

        then(bufferSynchronizationService).should().synchronizeAll();
        then(errorReporterService).should(times(0)).report(any(), any());
        then(writeLock).should().lock();
        then(writeLock).should().unlock();
    }
}