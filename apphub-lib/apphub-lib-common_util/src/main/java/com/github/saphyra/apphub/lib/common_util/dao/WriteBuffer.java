package com.github.saphyra.apphub.lib.common_util.dao;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.google.common.cache.Cache;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class WriteBuffer<DOMAIN_ID, DOMAIN> extends AbstractWriteBuffer<DOMAIN_ID, DOMAIN> {
    protected final Map<DOMAIN_ID, DOMAIN> buffer = new ConcurrentHashMap<>();
    protected final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    protected WriteBuffer(DateTimeUtil dateTimeUtil, Cache<DOMAIN_ID, DOMAIN> readCache) {
        super(dateTimeUtil, readCache);
    }

    public WriteBuffer(DateTimeUtil dateTimeUtil) {
        super(dateTimeUtil, null);
    }

    @Override
    protected Collection<DOMAIN> getBuffer() {
        return buffer.values();
    }

    @Override
    protected Map<DOMAIN_ID, DOMAIN> getBufferMap(Collection<DOMAIN> bufferCopy) {
        return bufferCopy.stream()
            .collect(Collectors.toMap(this::getDomainId, Function.identity()));
    }

    protected abstract DOMAIN_ID getDomainId(DOMAIN domain);

    @Override
    protected Lock getWriteLock() {
        return readWriteLock.writeLock();
    }

    protected void remove(DOMAIN_ID domainId) {
        Lock lock = readWriteLock.readLock();
        lock.lock();
        try {
            buffer.remove(domainId);
        } finally {
            lock.unlock();
        }
    }

    protected void removeAll() {
        Lock lock = readWriteLock.readLock();
        lock.lock();
        try {
            buffer.clear();
        } finally {
            lock.unlock();
        }
    }

    protected Optional<DOMAIN> getIfPresent(DOMAIN_ID domainId) {
        Lock lock = readWriteLock.readLock();
        lock.lock();
        try {
            return Optional.ofNullable(buffer.get(domainId));
        } finally {
            lock.unlock();
        }
    }

    public void add(DOMAIN_ID domainId, DOMAIN domain) {
        Lock lock = readWriteLock.readLock();
        lock.lock();
        try {
            buffer.put(domainId, domain);
        } finally {
            lock.unlock();
        }
    }

    public List<DOMAIN> search(Function<DOMAIN, Boolean> search) {
        Lock lock = readWriteLock.readLock();
        lock.lock();
        try {
            return buffer.values()
                .stream()
                .filter(search::apply)
                .toList();
        } finally {
            lock.unlock();
        }
    }
}
