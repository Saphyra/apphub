package com.github.saphyra.apphub.lib.common_util.dao;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.locks.Lock;

@Slf4j
public abstract class AbstractBuffer<T> implements Buffer {
    protected final DateTimeUtil dateTimeUtil;

    @Getter
    private volatile LocalDateTime lastSynchronized;

    protected AbstractBuffer(DateTimeUtil dateTimeUtil) {
        this.dateTimeUtil = dateTimeUtil;
        lastSynchronized = dateTimeUtil.getCurrentDateTime();
    }

    @Override
    public synchronized void synchronize() {
        if (getSize() == 0) {
            log.info("Buffer {} is empty, skipping synchronization.", getClass().getSimpleName());
            lastSynchronized = dateTimeUtil.getCurrentDateTime();
            return;
        }

        StopWatch stopWatch;
        Collection<T> bufferCopy;

        Lock writeLock = getWriteLock();
        try {
            writeLock.lock();

            int bufferSize = getSize();

            stopWatch = StopWatch.createStarted();
            log.info("Synchronizing {} with buffer size: {}, LastSynchronized: {}", getClass().getSimpleName(), bufferSize, lastSynchronized);

            bufferCopy = new ArrayList<>(getBuffer());

            getBuffer().clear();

            lastSynchronized = dateTimeUtil.getCurrentDateTime();
        } finally {
            writeLock.unlock();
        }

        doSynchronize(bufferCopy);

        afterSynchronize(bufferCopy);

        int bufferSize = bufferCopy.size();

        stopWatch.stop();
        log.info("{} synchronized in {} ms. Buffer size: {}, LastSynchronized: {}", getClass().getSimpleName(), stopWatch.getTime(), bufferSize, lastSynchronized);
    }

    @Override
    public int getSize() {
        return getBuffer()
            .size();
    }

    protected void afterSynchronize(Collection<T> bufferCopy) {
    }

    protected abstract Collection<T> getBuffer();

    protected abstract Lock getWriteLock();

    protected abstract void doSynchronize(Collection<T> bufferCopy);
}
