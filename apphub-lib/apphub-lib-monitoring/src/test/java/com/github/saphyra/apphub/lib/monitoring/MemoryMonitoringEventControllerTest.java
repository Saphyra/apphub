package com.github.saphyra.apphub.lib.monitoring;

import com.github.saphyra.apphub.api.admin_panel.client.MonitoringClient;
import com.github.saphyra.apphub.api.admin_panel.model.model.MemoryStatusModel;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
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

    @BeforeEach
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