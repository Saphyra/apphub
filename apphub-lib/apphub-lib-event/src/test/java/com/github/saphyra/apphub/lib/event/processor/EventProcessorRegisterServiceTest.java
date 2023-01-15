package com.github.saphyra.apphub.lib.event.processor;

import com.github.saphyra.apphub.api.platform.event_gateway.client.EventGatewayApiClient;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;
import com.github.saphyra.apphub.lib.common_util.SleepService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EventProcessorRegisterServiceTest {
    private static final int REGISTRATION_FAILURE_DELAY = 1;

    @Mock
    private EventProcessorProperties eventProcessorProperties;

    @Mock
    private EventProcessorRegistry registry;

    @Mock
    private EventGatewayApiClient eventGatewayApi;

    @Mock
    private SleepService sleepService;

    private EventProcessorRegisterService underTest;

    @Mock
    private RegisterProcessorRequest request;

    @BeforeEach
    public void setUp() {
        underTest = EventProcessorRegisterService.builder()
            .eventGatewayApi(eventGatewayApi)
            .registries(List.of(registry))
            .eventProcessorProperties(eventProcessorProperties)
            .sleepService(sleepService)
            .build();
    }

    @Test
    public void registerProcessors() {
        given(registry.getRequests()).willReturn(Arrays.asList(request));

        underTest.registerProcessors();

        verify(eventGatewayApi).registerProcessor(request);
    }

    @Test
    public void retry() {
        given(registry.getRequests()).willReturn(Arrays.asList(request));
        RuntimeException e = new RuntimeException("Asd");
        doThrow(e).when(eventGatewayApi).registerProcessor(request);
        given(eventProcessorProperties.getRegistrationFailureRetryDelay()).willReturn(REGISTRATION_FAILURE_DELAY);

        Throwable ex = catchThrowable(() -> underTest.registerProcessors());

        assertThat(ex).isEqualTo(e);
        verify(eventGatewayApi, times(3)).registerProcessor(request);
        verify(sleepService, times(3)).sleep(REGISTRATION_FAILURE_DELAY);
    }
}