package com.github.saphyra.apphub.service.platform.scheduler.schedulers;

import com.github.saphyra.apphub.api.admin_panel.client.MonitoringClient;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MemoryStatusUpdateTriggerSchedulerTest {
    private static final String LOCALE = "locale";

    @Mock
    private MonitoringClient monitoringClient;

    @Mock
    private CommonConfigProperties commonConfigProperties;

    @InjectMocks
    private MemoryStatusUpdateTriggerScheduler underTest;

    @Test
    public void triggerMemoryStatusUpdate() {
        given(commonConfigProperties.getDefaultLocale()).willReturn(LOCALE);

        underTest.triggerMemoryStatusUpdate();

        verify(monitoringClient).triggerMemoryStatusUpdate(LOCALE);
    }
}