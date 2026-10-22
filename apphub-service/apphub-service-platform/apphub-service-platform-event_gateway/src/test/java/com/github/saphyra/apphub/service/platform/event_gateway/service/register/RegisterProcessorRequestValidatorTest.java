package com.github.saphyra.apphub.service.platform.event_gateway.service.register;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.RegisterProcessorRequest;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
public class RegisterProcessorRequestValidatorTest {
    private static final String EVENT_NAME = "event-name";
    private static final String SERVICE_NAME = "service-name";
    private static final String URL = "url";

    @InjectMocks
    private RegisterProcessorRequestValidator underTest;

    private RegisterProcessorRequest.RegisterProcessorRequestBuilder builder;

    @BeforeEach
    public void setUp() {
        builder = RegisterProcessorRequest.builder()
            .eventName(EVENT_NAME)
            .host(SERVICE_NAME)
            .url(URL);
    }

    @Test
    public void blankServiceName() {
        Throwable ex = catchThrowable(() -> underTest.validate(builder.host(" ").build()));

        ExceptionValidator.validateInvalidParam(ex, "serviceName", ValidationUtil.MUST_NOT_BE_NULL_OR_BLANK);
    }

    @Test
    public void blankEventName() {
        Throwable ex = catchThrowable(() -> underTest.validate(builder.eventName(" ").build()));

        ExceptionValidator.validateInvalidParam(ex, "eventName", ValidationUtil.MUST_NOT_BE_NULL_OR_BLANK);
    }

    @Test
    public void nullUrl() {
        Throwable ex = catchThrowable(() -> underTest.validate(builder.url(null).build()));

        ExceptionValidator.validateInvalidParam(ex, "url", ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    public void valid() {
        underTest.validate(builder.build());
    }
}