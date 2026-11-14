package com.github.saphyra.apphub.lib.config.rabbitmq.message.monitoring;

import com.github.saphyra.apphub.lib.config.rabbitmq.message.CommonRabbitMqPublisherConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Import({MonitoringRabbitMqDefinition.class, CommonRabbitMqPublisherConfiguration.class})
@Profile("!test")
public class MonitoringRabbitMqPublisherConfig {
}
