package com.github.saphyra.apphub.lib.common_util.cache;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor
@Setter
@Slf4j
public class RequestScopedCacheManager implements CacheManager, BeanNameAware {
    private String beanName;

    @Override
    public Cache getCache(String name) {
        Map<String, Cache> cacheMap = getCacheMap();
        return cacheMap.computeIfAbsent(name, this::createCache);
    }

    protected Map<String, Cache> getCacheMap() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            log.warn("The bean '{}' of {} class' cache accessed outside of a request scope context", beanName, this.getClass().getSimpleName());
            return new HashMap<>();
        }
        @SuppressWarnings("unchecked")
        Map<String, Cache> cacheMap = (Map<String, Cache>) requestAttributes.getAttribute(getCacheMapAttributeName(), RequestAttributes.SCOPE_REQUEST);
        if (cacheMap == null) {
            cacheMap = new ConcurrentHashMap<>();
            requestAttributes.setAttribute(getCacheMapAttributeName(), cacheMap, RequestAttributes.SCOPE_REQUEST);
        }
        return cacheMap;
    }

    protected String getCacheMapAttributeName() {
        return this.getClass().getName() + beanName;
    }

    protected Cache createCache(String name) {
        return new ConcurrentMapCache(name);
    }

    @Override
    public Collection<String> getCacheNames() {
        Map<String, Cache> cacheMap = getCacheMap();
        return cacheMap.keySet();
    }

    public void clearCaches() {
        for (Cache cache : getCacheMap().values()) {
            cache.clear();
        }
        getCacheMap().clear();
    }
}
