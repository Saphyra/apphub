package com.github.saphyra.apphub.service.etc.modules.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Component;

import static com.github.saphyra.apphub.lib.config.rabbitmq.RabbitMqConstants.MESSAGE_LISTENER_DISABLED_ROLES_CHANGED;

@Component(MESSAGE_LISTENER_DISABLED_ROLES_CHANGED)
@Slf4j
@RequiredArgsConstructor
class DisabledRolesChangedMessageListener implements MessageListener {
    private final DisabledRoleCache disabledRoleCache;

    @Override
    public void onMessage(Message message) {
        log.info("Disabled roles changed. Clearing cache.");

        disabledRoleCache.clear();
    }
}
