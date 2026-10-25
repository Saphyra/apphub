package com.github.saphyra.apphub.service.feature.elite_base.processor.db_writer.service;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Synchronizes the MessageListeners and the MessageProcessor. While the MessageProcessor publishes messages, the Listeners must wait to avoid parallel saving of published and backfilled messages.
 */
@Component
public class EdMessageLock {
    @Getter
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
}
