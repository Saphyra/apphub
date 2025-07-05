package com.github.saphyra.apphub.lib.common_util.dao;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class DeleteBuffer<DOMAIN_ID> extends AbstractBuffer {
    protected final Set<DOMAIN_ID> buffer = Collections.newSetFromMap(new ConcurrentHashMap<>());
    protected final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    protected DeleteBuffer(DateTimeUtil dateTimeUtil) {
        super(dateTimeUtil);
    }

    @Override
    protected Collection<?> getBuffer() {
        return buffer;
    }

    @Override
    protected Lock getWriteLock() {
        return readWriteLock.writeLock();
    }

    public void add(DOMAIN_ID domainId) {
        Lock lock = readWriteLock.readLock();
        try {
            lock.lock();
            buffer.add(domainId);
        } finally {
            lock.unlock();
        }
    }

    protected void addAll(Collection<DOMAIN_ID> ids){
        Lock lock = readWriteLock.readLock();
        try {
            lock.lock();
            buffer.addAll(ids);
        } finally {
            lock.unlock();
        }
    }

    protected void removeAll() {
        Lock lock = readWriteLock.readLock();
        try {
            lock.lock();
            buffer.clear();
        } finally {
            lock.unlock();
        }
    }

    protected boolean contains(DOMAIN_ID domainId) {
        Lock lock = readWriteLock.readLock();
        try {
            lock.lock();
            return buffer.contains(domainId);
        } finally {
            lock.unlock();
        }
    }

    protected void remove(DOMAIN_ID domainId) {
        Lock lock = readWriteLock.readLock();
        try {
            lock.lock();
            buffer.remove(domainId);
        } finally {
            lock.unlock();
        }
    }
}
