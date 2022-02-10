package com.github.saphyra.apphub.lib.monitoring;

import com.github.saphyra.apphub.lib.event.processor.EnableEventProcessor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = MonitoringConfiguration.class)
@EnableEventProcessor
public class MonitoringConfiguration {
}
