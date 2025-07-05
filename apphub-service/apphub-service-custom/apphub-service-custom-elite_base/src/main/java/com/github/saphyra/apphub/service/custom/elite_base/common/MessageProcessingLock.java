package com.github.saphyra.apphub.service.custom.elite_base.common;

import org.springframework.stereotype.Component;

import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
public class MessageProcessingLock extends ReentrantReadWriteLock {
}
