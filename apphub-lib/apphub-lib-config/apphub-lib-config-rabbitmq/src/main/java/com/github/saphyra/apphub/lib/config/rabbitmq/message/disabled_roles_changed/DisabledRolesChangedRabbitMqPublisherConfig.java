package com.github.saphyra.apphub.lib.config.rabbitmq.message.disabled_roles_changed;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(DisabledRolesChangedRabbitMqDefinition.class)
public class DisabledRolesChangedRabbitMqPublisherConfig {
}
