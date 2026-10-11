package com.github.saphyra.apphub.lib.config.rabbitmq;

public class RabbitMqConstants {
    public static final String EMPTY_MESSAGE = "";
    public static final String SEPARATOR = ".";

    //Placeholders
    public static final String PLACEHOLDER_SERVICE = "${service}";

    //Suffixes
    public static final String SUFFIX_EXCHANGE = "exchange";
    public static final String SUFFIX_QUEUE = "queue";
    public static final String SUFFIX_MESSAGE_LISTENER = "message-listener";

    //Message names
    public static final String MESSAGE_DISABLED_ROLES_CHANGED = "disabled-roles-changed";

    //Exchanges
    public static final String EXCHANGE_DISABLED_ROLES_CHANGED = String.join(SEPARATOR, MESSAGE_DISABLED_ROLES_CHANGED, SUFFIX_EXCHANGE);

    //Queues
    public static final String QUEUE_DISABLED_ROLES_CHANGED = String.join(SEPARATOR, MESSAGE_DISABLED_ROLES_CHANGED, PLACEHOLDER_SERVICE, SUFFIX_QUEUE);

    //Message listeners
    public static final String MESSAGE_LISTENER_DISABLED_ROLES_CHANGED = MESSAGE_DISABLED_ROLES_CHANGED + "_" + SUFFIX_MESSAGE_LISTENER;
}
