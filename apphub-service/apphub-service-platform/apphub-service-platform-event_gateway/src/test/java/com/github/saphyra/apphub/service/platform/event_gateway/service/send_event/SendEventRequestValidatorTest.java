package com.github.saphyra.apphub.service.platform.event_gateway.service.send_event;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.error_handler.exception.BadRequestException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@RunWith(MockitoJUnitRunner.class)
public class SendEventRequestValidatorTest {
    private static final String EVENT_NAME = "event-name";

    @InjectMocks
    private SendEventRequestValidator underTest;

    @Test
    public void metadataNull() {
        SendEventRequest<String> sendEventRequest = SendEventRequest.<String>builder()
            .eventName(EVENT_NAME)
            .metadata(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(sendEventRequest));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo("INVALID_PARAM");
        assertThat(exception.getErrorMessage().getParams().get("metadata")).isEqualTo("Invalid parameter");
    }

    @Test
    public void eventNameBlank() {
        SendEventRequest<String> sendEventRequest = SendEventRequest.<String>builder()
            .eventName(" ")
            .metadata(new HashMap<>())
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(sendEventRequest));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo("INVALID_PARAM");
        assertThat(exception.getErrorMessage().getParams().get("eventName")).isEqualTo("Invalid parameter");
    }

    @Test
    public void valid() {
        SendEventRequest<String> sendEventRequest = SendEventRequest.<String>builder()
            .eventName(EVENT_NAME)
            .metadata(new HashMap<>())
            .build();

        underTest.validate(sendEventRequest);
    }
}