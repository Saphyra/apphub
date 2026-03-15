package com.github.saphyra.apphub.integration.core.feature_lock;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;

class FeatureLockManager {
    private static final ConcurrentMap<Feature, Semaphore> LOCKS = new ConcurrentHashMap<>();

    static Semaphore getLock(Feature feature) {
        return LOCKS.computeIfAbsent(feature, f -> new Semaphore(feature.getPermits(), true));
    }
}
