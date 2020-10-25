package com.github.saphyra.apphub.lib.event.processor;

import com.github.saphyra.apphub.api.platform.event_gateway.client.EventGatewayApiClient;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EventProcessorRegisterServiceTest {
    @Mock
    private EventProcessorProperties eventProcessorProperties;

    @Mock
    private EventProcessorRegistry registry;

    @Mock
    private EventGatewayApiClient eventGatewayApi;

    @InjectMocks
    private EventProcessorRegisterService underTest;

    @Mock
    private RegisterProcessorRequest request;

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
        given(eventProcessorProperties.getRegistrationFailureRetryDelay()).willReturn(1);

        Throwable ex = catchThrowable(() -> underTest.registerProcessors());

        assertThat(ex).isEqualTo(e);
        verify(eventGatewayApi, times(3)).registerProcessor(request);
    }
}