package com.github.saphyra.apphub.lib.monitoring;

import com.github.saphyra.apphub.api.admin_panel.client.MonitoringClient;
import com.github.saphyra.apphub.api.admin_panel.model.model.MemoryStatusModel;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MemoryMonitoringEventControllerTest {
    private static final String SERVICE_NAME = "service-name";
    private static final String LOCALE = "locale";

    @Mock
    private CommonConfigProperties commonConfigProperties;

    @Mock
    private MonitoringClient monitoringClient;

    @Mock
    private MemoryStatusModelFactory memoryStatusModelFactory;

    private MemoryMonitoringEventController underTest;

    @Mock
    private MemoryStatusModel memoryStatusModel;

    @Before
    public void setUp() {
        underTest = MemoryMonitoringEventController.builder()
            .commonConfigProperties(commonConfigProperties)
            .monitoringClient(monitoringClient)
            .memoryStatusModelFactory(memoryStatusModelFactory)
            .serviceName(SERVICE_NAME)
            .build();
    }

    @Test
    public void sendMemoryStatus() {
        given(memoryStatusModelFactory.create(SERVICE_NAME)).willReturn(memoryStatusModel);
        given(commonConfigProperties.getDefaultLocale()).willReturn(LOCALE);

        underTest.sendMemoryStatus();

        verify(monitoringClient).reportMemoryStatus(memoryStatusModel, LOCALE);
    }
}