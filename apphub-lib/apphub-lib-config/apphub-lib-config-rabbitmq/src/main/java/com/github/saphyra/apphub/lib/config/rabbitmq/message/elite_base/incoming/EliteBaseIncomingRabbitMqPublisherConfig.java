package com.github.saphyra.apphub.lib.config.rabbitmq.message.elite_base.incoming;

import com.github.saphyra.apphub.lib.config.rabbitmq.message.CommonRabbitMqPublisherConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@ConditionalOnProperty(prefix = "elite-base", name = "message.incoming.publisher.enabled", havingValue = "true")
@Profile("!test")
@Import({EliteBaseIncomingRabbitMqDefinition.class, CommonRabbitMqPublisherConfiguration.class})
public class EliteBaseIncomingRabbitMqPublisherConfig {
}
