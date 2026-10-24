package com.github.saphyra.apphub.lib.config.rabbitmq.message.disabled_roles_changed;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Import(DisabledRolesChangedRabbitMqDefinition.class)
@Profile("!test")
public class DisabledRolesChangedRabbitMqPublisherConfig {
}
