package com.github.saphyra.apphub.service.platform.event_gateway.service.register;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RegisterProcessorRequestValidatorTest {
    private static final String EVENT_NAME = "event-name";
    private static final String SERVICE_NAME = "service-name";
    private static final String URL = "url";

    @InjectMocks
    private RegisterProcessorRequestValidator underTest;

    private RegisterProcessorRequest.RegisterProcessorRequestBuilder builder;

    @Before
    public void setUp() {
        builder = RegisterProcessorRequest.builder()
            .eventName(EVENT_NAME)
            .serviceName(SERVICE_NAME)
            .url(URL);
    }

    @Test(expected = IllegalArgumentException.class)
    public void blankServiceName() {
        underTest.validate(builder.serviceName(" ").build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void blankEventName() {
        underTest.validate(builder.eventName(" ").build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void blankUrl() {
        underTest.validate(builder.url(" ").build());
    }

    @Test
    public void valid() {
        underTest.validate(builder.build());
    }
}