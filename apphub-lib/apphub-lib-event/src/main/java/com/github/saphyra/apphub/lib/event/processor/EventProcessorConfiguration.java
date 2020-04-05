package com.github.saphyra.apphub.lib.event.processor;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@ConditionalOnProperty(value = "event.processor.enabled", havingValue = "true")
class EventProcessorConfiguration {
}
