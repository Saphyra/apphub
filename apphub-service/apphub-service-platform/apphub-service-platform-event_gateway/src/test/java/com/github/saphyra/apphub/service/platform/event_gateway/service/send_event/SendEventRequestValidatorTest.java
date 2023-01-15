package com.github.saphyra.apphub.service.platform.event_gateway.service.send_event;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
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

        ExceptionValidator.validateInvalidParam(ex, "metadata", "must not be null");
    }

    @Test
    public void eventNameBlank() {
        SendEventRequest<String> sendEventRequest = SendEventRequest.<String>builder()
            .eventName(" ")
            .metadata(new HashMap<>())
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(sendEventRequest));

        ExceptionValidator.validateInvalidParam(ex, "eventName", "must not be null or blank");
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