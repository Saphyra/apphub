package com.github.saphyra.apphub.lib.event.processor;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class EventProcessorProperties {
    @Value("${event.processor.registration.failure.delay.milliseconds}")
    private int registrationFailureRetryDelay;
}
