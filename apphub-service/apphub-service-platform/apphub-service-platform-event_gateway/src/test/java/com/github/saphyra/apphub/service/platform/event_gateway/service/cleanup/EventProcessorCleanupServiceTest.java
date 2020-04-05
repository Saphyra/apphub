package com.github.saphyra.apphub.service.platform.event_gateway.service.cleanup;

import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessor;
import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessorDao;
import com.github.saphyra.util.OffsetDateTimeProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.OffsetDateTime;
import java.util.Arrays;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EventProcessorCleanupServiceTest {
    private static final Integer EXPIRATION_SECONDS = 123;
    private static final OffsetDateTime CURRENT_DATE = OffsetDateTime.now();

    @Mock
    private EventProcessorDao eventProcessorDao;

    @Mock
    private OffsetDateTimeProvider offsetDateTimeProvider;

    private EventProcessorCleanupService underTest;

    @Mock
    private EventProcessor eventProcessor;

    @Before
    public void setUp() {
        underTest = EventProcessorCleanupService.builder()
            .eventProcessorDao(eventProcessorDao)
            .offsetDateTimeProvider(offsetDateTimeProvider)
            .eventProcessorExpirationSeconds(EXPIRATION_SECONDS)
            .build();
    }

    @Test
    public void cleanupExpiredEventProcessors() {
        given(offsetDateTimeProvider.getCurrentDate()).willReturn(CURRENT_DATE);
        given(eventProcessorDao.getByLastAccessBefore(CURRENT_DATE.minusSeconds(EXPIRATION_SECONDS))).willReturn(Arrays.asList(eventProcessor));

        underTest.cleanupExpiredEventProcessors();

        verify(eventProcessorDao).deleteAll(Arrays.asList(eventProcessor));
    }
}