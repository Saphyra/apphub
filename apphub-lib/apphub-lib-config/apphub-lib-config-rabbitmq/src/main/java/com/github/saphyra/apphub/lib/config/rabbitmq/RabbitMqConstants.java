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
    public static final String MESSAGE_ELITE_BASE_INCOMING = "elite-base-incoming";
    public static final String MESSAGE_ELITE_BASE_STORED = "elite-base-stored";
    public static final String MESSAGE_ELITE_BASE_BACKFILL = "elite-base-backfill";

    //Exchanges
    public static final String EXCHANGE_DISABLED_ROLES_CHANGED = String.join(SEPARATOR, MESSAGE_DISABLED_ROLES_CHANGED, SUFFIX_EXCHANGE);
    public static final String EXCHANGE_ELITE_BASE_INCOMING = String.join(SEPARATOR, MESSAGE_ELITE_BASE_INCOMING, SUFFIX_EXCHANGE);
    public static final String EXCHANGE_ELITE_BASE_STORED = String.join(SEPARATOR, MESSAGE_ELITE_BASE_STORED, SUFFIX_EXCHANGE);
    public static final String EXCHANGE_ELITE_BASE_BACKFILL = String.join(SEPARATOR, MESSAGE_ELITE_BASE_BACKFILL, SUFFIX_EXCHANGE);

    //Queues
    public static final String QUEUE_DISABLED_ROLES_CHANGED = String.join(SEPARATOR, MESSAGE_DISABLED_ROLES_CHANGED, PLACEHOLDER_SERVICE, SUFFIX_QUEUE);
    public static final String QUEUE_ELITE_BASE_INCOMING = String.join(SEPARATOR, MESSAGE_ELITE_BASE_INCOMING, SUFFIX_QUEUE);
    public static final String QUEUE_ELITE_BASE_STORED = String.join(SEPARATOR, MESSAGE_ELITE_BASE_STORED, SUFFIX_QUEUE);
    public static final String QUEUE_ELITE_BASE_BACKFILL = String.join(SEPARATOR, MESSAGE_ELITE_BASE_BACKFILL, SUFFIX_QUEUE);

    //Message listeners
    public static final String MESSAGE_LISTENER_DISABLED_ROLES_CHANGED = MESSAGE_DISABLED_ROLES_CHANGED + "_" + SUFFIX_MESSAGE_LISTENER;
    public static final String MESSAGE_LISTENER_ELITE_BASE_INCOMING = MESSAGE_ELITE_BASE_INCOMING + "_" + SUFFIX_MESSAGE_LISTENER;
    public static final String MESSAGE_LISTENER_ELITE_BASE_STORED = MESSAGE_ELITE_BASE_STORED + "_" + SUFFIX_MESSAGE_LISTENER;
    public static final String MESSAGE_LISTENER_ELITE_BASE_BACKFILL = MESSAGE_ELITE_BASE_BACKFILL + "_" + SUFFIX_MESSAGE_LISTENER;
}
