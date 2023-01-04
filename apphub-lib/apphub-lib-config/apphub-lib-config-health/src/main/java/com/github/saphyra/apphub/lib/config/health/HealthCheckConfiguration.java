package com.github.saphyra.apphub.lib.config.health;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = HealthCheckConfiguration.class)
class HealthCheckConfiguration {
}
