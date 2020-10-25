package com.github.saphyra.apphub.service.platform.event_gateway.service.heartbeat;

import com.github.saphyra.apphub.lib.common_util.OffsetDateTimeProvider;
import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessor;
import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessorDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.OffsetDateTime;
import java.util.Arrays;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class HeartbeatServiceTest {
    private static final String SERVICE_NAME = "service-name";
    private static final OffsetDateTime CURRENT_DATE = OffsetDateTime.now();

    @Mock
    private EventProcessorDao eventProcessorDao;

    @Mock
    private OffsetDateTimeProvider offsetDateTimeProvider;

    @InjectMocks
    private HeartbeatService underTest;

    @Mock
    private EventProcessor eventProcessor;

    @Test
    public void heartbeat() {
        given(eventProcessorDao.getByServiceName(SERVICE_NAME)).willReturn(Arrays.asList(eventProcessor));
        given(offsetDateTimeProvider.getCurrentDate()).willReturn(CURRENT_DATE);

        underTest.heartbeat(SERVICE_NAME);

        verify(eventProcessor).setLastAccess(CURRENT_DATE);
        verify(eventProcessorDao).saveAll(Arrays.asList(eventProcessor));
    }
}