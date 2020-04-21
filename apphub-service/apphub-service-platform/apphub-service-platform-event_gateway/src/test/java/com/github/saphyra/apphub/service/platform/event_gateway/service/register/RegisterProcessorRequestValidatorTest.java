package com.github.saphyra.apphub.service.platform.event_gateway.service.register;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;
import com.github.saphyra.apphub.lib.error_handler.exception.BadRequestException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

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

    @Test
    public void blankServiceName() {
        Throwable ex = catchThrowable(() -> underTest.validate(builder.serviceName(" ").build()));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo("INVALID_PARAM");
        assertThat(exception.getErrorMessage().getParams().get("serviceName")).isEqualTo("Invalid parameter");
    }

    @Test
    public void blankEventName() {
        Throwable ex = catchThrowable(() -> underTest.validate(builder.eventName(" ").build()));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo("INVALID_PARAM");
        assertThat(exception.getErrorMessage().getParams().get("eventName")).isEqualTo("Invalid parameter");
    }

    @Test
    public void nullUrl() {
        Throwable ex = catchThrowable(() -> underTest.validate(builder.url(null).build()));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo("INVALID_PARAM");
        assertThat(exception.getErrorMessage().getParams().get("url")).isEqualTo("Invalid parameter");
    }

    @Test
    public void valid() {
        underTest.validate(builder.build());
    }
}