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

public abstract class ListCachedBufferedDao<ENTITY, DOMAIN, ENTITY_ID, LIST_CACHE_KEY, DOMAIN_ID, REPOSITORY extends CrudRepository<ENTITY, ENTITY_ID>> extends AbstractDao<ENTITY, DOMAIN, ENTITY_ID, REPOSITORY> {
    protected final Cache<LIST_CACHE_KEY, List<DOMAIN>> readCache;
    protected final WriteBuffer<DOMAIN_ID, DOMAIN> writeBuffer;
    protected final DeleteBuffer<DOMAIN_ID> deleteBuffer;

    protected ListCachedBufferedDao(
        Converter<ENTITY, DOMAIN> converter,
        REPOSITORY repository,
        Cache<LIST_CACHE_KEY, List<DOMAIN>> readCache,
        WriteBuffer<DOMAIN_ID, DOMAIN> writeBuffer,
        DeleteBuffer<DOMAIN_ID> deleteBuffer
    ) {
        super(converter, repository);
        this.readCache = readCache;
        this.writeBuffer = writeBuffer;
        this.deleteBuffer = deleteBuffer;
    }

    @Override
    public void delete(DOMAIN domain) {
        DOMAIN_ID domainId = getDomainId(domain);
        deleteByDomainId(domainId);
    }

    @Override
    public void deleteAll() {
        readCache.invalidateAll();
        writeBuffer.removeAll();
        deleteBuffer.removeAll();
        repository.deleteAll();
    }

    @Override
    public void deleteAll(List<DOMAIN> domains) {
        domains.forEach(this::delete);
    }

    @Override
    public void deleteById(ENTITY_ID id) {
        DOMAIN_ID domainId = toDomainId(id);
        deleteByDomainId(domainId);
    }

    @Override
    public List<DOMAIN> findAll() {
        return syncWithCaches(super.findAll());
    }

    @Override
    public List<DOMAIN> findAllById(Iterable<ENTITY_ID> ids) {
        return syncWithCaches(super.findAllById(ids));
    }

    @Override
    public Optional<DOMAIN> findById(ENTITY_ID id) {
        DOMAIN_ID domainId = toDomainId(id);

        if (deleteBuffer.contains(domainId)) {
            return Optional.empty();
        }

        Optional<DOMAIN> maybeDomain = writeBuffer.getIfPresent(domainId);

        if (maybeDomain.isEmpty()) {
            maybeDomain = super.findById(id);
        }

        return maybeDomain;
    }

    @Override
    public void save(DOMAIN domain) {
        DOMAIN_ID domainId = getDomainId(domain);

        deleteBuffer.remove(domainId);
        writeBuffer.add(domainId, domain);
        readCache.invalidate(getCacheKey(domain));
    }

    @Override
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
            .flatMap(Collection::stream)
            .filter(search::apply)
            .toList();
        matches.addAll(readCacheResults);

        Map<DOMAIN_ID, DOMAIN> available = matches.stream()
            .map(domain -> new BiWrapper<>(getDomainId(domain), domain))
            .collect(Collectors.toMap(BiWrapper::getEntity1, BiWrapper::getEntity2, mergeFunction::apply));

        if (available.size() > 1) {
            throw new IllegalStateException("Multiple matches found for search criteria.");
        } else if (available.isEmpty()) {
            return query.get()
                .map(converter::convertEntity)
                .filter(domain -> !deleteBuffer.contains(getDomainId(domain)));
        } else {
            return available.entrySet()
                .stream()
                .filter(entry -> !deleteBuffer.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .findAny();
        }
    }

    protected List<DOMAIN> searchList(LIST_CACHE_KEY cacheKey, Supplier<List<ENTITY>> query) {
        return Optional.ofNullable(readCache.getIfPresent(cacheKey))
            .orElseGet(() -> search(query));
    }

    private List<DOMAIN> search(Supplier<List<ENTITY>> query) {
        List<DOMAIN> result = converter.convertEntity(query.get());

        return syncWithCaches(result);
    }

    protected abstract LIST_CACHE_KEY getCacheKey(DOMAIN domain);

    protected abstract DOMAIN_ID toDomainId(ENTITY_ID id);

    protected abstract DOMAIN_ID getDomainId(DOMAIN domain);

    protected abstract boolean matchesWithId(DOMAIN_ID domainId, DOMAIN domain);

    private void deleteByDomainId(DOMAIN_ID domainId) {
        readCache.asMap()
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue().stream().anyMatch(domain -> matchesWithId(domainId, domain)))
            .forEach(entry -> readCache.invalidate(entry.getKey()));
        writeBuffer.remove(domainId);
        deleteBuffer.add(domainId);
    }

    protected List<DOMAIN> syncWithCaches(List<DOMAIN> original) {
        Map<DOMAIN_ID, DOMAIN> upToDate = original.stream()
            .map(domain -> new BiWrapper<>(getDomainId(domain), domain))
            .filter(bw -> !deleteBuffer.contains(bw.getEntity1()))
            .map(bw -> bw.mapEntity2(domain -> writeBuffer.getIfPresent(bw.getEntity1()).orElse(domain)))
            .distinct()
            .collect(Collectors.toMap(BiWrapper::getEntity1, BiWrapper::getEntity2));

        return new ArrayList<>(upToDate.values());
    }
}
