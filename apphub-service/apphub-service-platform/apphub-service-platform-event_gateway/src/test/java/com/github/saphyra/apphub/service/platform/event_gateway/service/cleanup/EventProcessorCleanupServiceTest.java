package com.github.saphyra.apphub.service.platform.event_gateway.service.cleanup;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessor;
import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessorDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EventProcessorCleanupServiceTest {
    private static final Integer EXPIRATION_SECONDS = 123;
    private static final LocalDateTime CURRENT_DATE = LocalDateTime.now();

    @Mock
    private EventProcessorDao eventProcessorDao;

    @Mock
    private DateTimeUtil dateTimeUtil;

    private EventProcessorCleanupService underTest;

    @Mock
    private EventProcessor eventProcessor;

    @BeforeEach
    public void setUp() {
        underTest = EventProcessorCleanupService.builder()
            .eventProcessorDao(eventProcessorDao)
            .dateTimeUtil(dateTimeUtil)
            .eventProcessorExpirationSeconds(EXPIRATION_SECONDS)
            .build();
    }

    @Test
    public void cleanupExpiredEventProcessors() {
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_DATE);
        given(eventProcessorDao.getByLastAccessBefore(CURRENT_DATE.minusSeconds(EXPIRATION_SECONDS))).willReturn(Arrays.asList(eventProcessor));

        underTest.cleanupExpiredEventProcessors();

        verify(eventProcessorDao).deleteAll(Arrays.asList(eventProcessor));
    }
}