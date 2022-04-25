package com.github.saphyra.apphub.service.platform.event_gateway.service.cleanup;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessor;
import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessorDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
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

    @Before
    public void setUp() {
        underTest = EventProcessorCleanupService.builder()
            .eventProcessorDao(eventProcessorDao)
            .dateTimeUtil(dateTimeUtil)
            .eventProcessorExpirationSeconds(EXPIRATION_SECONDS)
            .build();
    }

    @Test
    public void cleanupExpiredEventProcessors() {
        given(dateTimeUtil.getCurrentTime()).willReturn(CURRENT_DATE);
        given(eventProcessorDao.getByLastAccessBefore(CURRENT_DATE.minusSeconds(EXPIRATION_SECONDS))).willReturn(Arrays.asList(eventProcessor));

        underTest.cleanupExpiredEventProcessors();

        verify(eventProcessorDao).deleteAll(Arrays.asList(eventProcessor));
    }
}