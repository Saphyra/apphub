package com.github.saphyra.apphub.lib.common_util.dao;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.concurrent.locks.Lock;

@Slf4j
public abstract class AbstractBuffer implements Buffer {
    protected final DateTimeUtil dateTimeUtil;

    @Getter
    private volatile LocalDateTime lastSynchronized;

    protected AbstractBuffer(DateTimeUtil dateTimeUtil) {
        this.dateTimeUtil = dateTimeUtil;
        lastSynchronized = dateTimeUtil.getCurrentDateTime();
    }

    @Override
    public void synchronize() {
        if (getSize() == 0) {
            log.info("Buffer {} is empty, skipping synchronization.", getClass().getSimpleName());
            lastSynchronized = dateTimeUtil.getCurrentDateTime();
            return;
        }

        Lock writeLock = getWriteLock();
        try {
            writeLock.lock();

            int bufferSize = getSize();

            StopWatch stopWatch = StopWatch.createStarted();
            log.info("Synchronizing {} with buffer size: {}, LastSynchronized: {}", getClass().getSimpleName(), bufferSize, lastSynchronized);
            doSynchronize();

            afterSynchronize();

            getBuffer().clear();

            stopWatch.stop();

            log.info("{} synchronized in {} ms. Buffer size: {}, LastSynchronized: {}", getClass().getSimpleName(), stopWatch.getTime(), bufferSize, lastSynchronized);

            lastSynchronized = dateTimeUtil.getCurrentDateTime();
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public int getSize() {
        return getBuffer()
            .size();
    }

    protected void afterSynchronize() {
    }

    protected abstract Collection<?> getBuffer();

    protected abstract Lock getWriteLock();

    protected abstract void doSynchronize();
}
