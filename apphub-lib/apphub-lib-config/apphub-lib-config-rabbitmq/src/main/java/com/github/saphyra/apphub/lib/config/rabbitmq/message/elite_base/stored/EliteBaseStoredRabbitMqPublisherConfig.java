package com.github.saphyra.apphub.lib.config.rabbitmq.message.elite_base.stored;

import com.github.saphyra.apphub.lib.config.rabbitmq.message.CommonRabbitMqPublisherConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@ConditionalOnProperty(prefix = "elite-base", name = "message.stored.publisher.enabled", havingValue = "true")
@Profile("!test")
@Import({EliteBaseStoredRabbitMqDefinition.class, CommonRabbitMqPublisherConfiguration.class})
public class EliteBaseStoredRabbitMqPublisherConfig {
}
