package com.github.saphyra.apphub.lib.common_util.dao;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.Converter;
import com.google.common.cache.Cache;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class CachedBufferedDao<ENTITY, DOMAIN, ENTITY_ID, CACHE_KEY, REPOSITORY extends CrudRepository<ENTITY, ENTITY_ID>> extends AbstractDao<ENTITY, DOMAIN, ENTITY_ID, REPOSITORY> {
    protected final Cache<CACHE_KEY, DOMAIN> readCache;
    protected final WriteBuffer<CACHE_KEY, DOMAIN> writeBuffer;
    protected final DeleteBuffer<CACHE_KEY> deleteBuffer;

    protected CachedBufferedDao(
        Converter<ENTITY, DOMAIN> converter,
        REPOSITORY repository,
        Cache<CACHE_KEY, DOMAIN> readCache,
        WriteBuffer<CACHE_KEY, DOMAIN> writeBuffer,
        DeleteBuffer<CACHE_KEY> deleteBuffer
    ) {
        super(converter, repository);
        this.readCache = readCache;
        this.writeBuffer = writeBuffer;
        this.deleteBuffer = deleteBuffer;
    }

    public void delete(DOMAIN domain) {
        CACHE_KEY cacheKey = getCacheKey(domain);
        deleteByCacheKey(cacheKey);
    }

    public void deleteAll() {
        readCache.invalidateAll();
        writeBuffer.removeAll();
        deleteBuffer.removeAll();
        repository.deleteAll();
    }

    public void deleteAll(List<DOMAIN> domains) {
        domains.forEach(this::delete);
    }

    public void deleteById(ENTITY_ID id) {
        CACHE_KEY cacheKey = toCacheKey(id);
        deleteByCacheKey(cacheKey);
    }

    public List<DOMAIN> findAll() {
        return syncWithCaches(super.findAll());
    }

    public List<DOMAIN> findAllById(Iterable<ENTITY_ID> ids) {
        return syncWithCaches(super.findAllById(ids));
    }

    public Optional<DOMAIN> findById(ENTITY_ID id) {
        CACHE_KEY cacheKey = toCacheKey(id);

        if (deleteBuffer.contains(cacheKey)) {
            return Optional.empty();
        }

        Optional<DOMAIN> maybeDomain = writeBuffer.getIfPresent(cacheKey);

        if (maybeDomain.isEmpty()) {
            maybeDomain = Optional.ofNullable(readCache.getIfPresent(cacheKey));
        }

        if (maybeDomain.isEmpty()) {
            maybeDomain = super.findById(id);
        }

        maybeDomain.ifPresent(domain -> readCache.put(cacheKey, domain));

        return maybeDomain;
    }

    public void save(DOMAIN domain) {
        CACHE_KEY cacheKey = getCacheKey(domain);
        deleteBuffer.remove(cacheKey);

        writeBuffer.add(cacheKey, domain);
        readCache.put(cacheKey, domain);
    }

    public void saveAll(Collection<DOMAIN> domains) {
        domains.forEach(this::save);
    }

    /**
     * Searches for a single domain object based on the provided search function and query.
     *
     * @param search        Search criteria for searching in caches
     * @param query         Database query to retrieve the entity if not found in caches
     * @param mergeFunction If multiple versions are found in different caches, this function is used to merge them.
     * @throws IllegalStateException If multiple matches are found for the search criteria.
     */
    protected Optional<DOMAIN> searchOne(Function<DOMAIN, Boolean> search, Supplier<Optional<ENTITY>> query, BiFunction<DOMAIN, DOMAIN, DOMAIN> mergeFunction) {
        List<DOMAIN> matches = new ArrayList<>(writeBuffer.search(search));

        List<DOMAIN> readCacheResults = readCache.asMap()
            .values()
            .stream()
            .filter(search::apply)
            .toList();
        matches.addAll(readCacheResults);

        Map<CACHE_KEY, DOMAIN> available = matches.stream()
            .map(domain -> new BiWrapper<>(getCacheKey(domain), domain))
            .collect(Collectors.toMap(BiWrapper::getEntity1, BiWrapper::getEntity2, mergeFunction::apply));

        if (available.size() > 1) {
            throw new IllegalStateException("Multiple matches found for search criteria.");
        } else if (available.isEmpty()) {
            Optional<DOMAIN> result = query.get()
                .map(converter::convertEntity)
                .filter(domain -> !deleteBuffer.contains(getCacheKey(domain)));

            result.ifPresent(domain -> readCache.put(getCacheKey(domain), domain));

            return result;
        } else {
            return available.entrySet()
                .stream()
                .filter(entry -> !deleteBuffer.contains(entry.getKey()))
                .peek(entry -> readCache.put(entry.getKey(), entry.getValue()))
                .map(Map.Entry::getValue)
                .findAny();
        }
    }

    protected List<DOMAIN> search(Supplier<List<ENTITY>> query) {
        List<DOMAIN> result = converter.convertEntity(query.get());

        return syncWithCaches(result);
    }

    protected abstract CACHE_KEY getCacheKey(DOMAIN domain);

    protected abstract CACHE_KEY toCacheKey(ENTITY_ID id);

    private void deleteByCacheKey(CACHE_KEY cacheKey) {
        readCache.invalidate(cacheKey);
        writeBuffer.remove(cacheKey);
        deleteBuffer.add(cacheKey);
    }

    private List<DOMAIN> syncWithCaches(List<DOMAIN> original) {
        Map<CACHE_KEY, DOMAIN> upToDate = original.stream()
            .map(domain -> new BiWrapper<>(getCacheKey(domain), domain))
            .filter(bw -> !deleteBuffer.contains(bw.getEntity1()))
            .map(bw -> bw.mapEntity2(domain -> writeBuffer.getIfPresent(bw.getEntity1()).orElse(domain)))
            .distinct()
            .collect(Collectors.toMap(BiWrapper::getEntity1, BiWrapper::getEntity2));

        readCache.putAll(upToDate);

        return new ArrayList<>(upToDate.values());
    }
}
