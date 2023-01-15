package com.github.saphyra.apphub.service.platform.event_gateway.service.heartbeat;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessor;
import com.github.saphyra.apphub.service.platform.event_gateway.dao.EventProcessorDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ProcessHeartbeatServiceTest {
    private static final String SERVICE_NAME = "service-name";
    private static final LocalDateTime CURRENT_DATE = LocalDateTime.now();

    @Mock
    private EventProcessorDao eventProcessorDao;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @InjectMocks
    private ProcessHeartbeatService underTest;

    @Mock
    private EventProcessor eventProcessor;

    @Test
    public void heartbeat() {
        given(eventProcessorDao.getByServiceName(SERVICE_NAME)).willReturn(Arrays.asList(eventProcessor));
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_DATE);

        underTest.heartbeat(SERVICE_NAME);

        verify(eventProcessor).setLastAccess(CURRENT_DATE);
        verify(eventProcessorDao).saveAll(Arrays.asList(eventProcessor));
    }
}