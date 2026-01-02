package com.github.saphyra.apphub.lib.config.health;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ComponentScan(basePackageClasses = HealthCheckAutoConfiguration.class)
class HealthCheckAutoConfiguration {
}
