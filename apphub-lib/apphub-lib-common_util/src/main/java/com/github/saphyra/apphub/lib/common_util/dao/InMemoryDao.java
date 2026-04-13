package com.github.saphyra.apphub.lib.common_util.dao;

import com.github.saphyra.apphub.lib.common_util.converter.Converter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A DAO implementation that uses an in-memory cache that never expires.
 */
@Slf4j
public abstract class InMemoryDao<ENTITY, DOMAIN, ID, REPOSITORY extends CrudRepository<ENTITY, ID>> extends AbstractDao<ENTITY, DOMAIN, ID, REPOSITORY> {
    protected final ConcurrentHashMap<ID, DOMAIN> cache = new ConcurrentHashMap<>();

    protected InMemoryDao(Converter<ENTITY, DOMAIN> converter, REPOSITORY repository) {
        super(converter, repository);
    }

    public void load() {
        cache.clear();

        findAll();

        log.info("Cached DAO {} is successfully loaded with {} items.", getClass().getSimpleName(), cache.size());
    }

    @Override
    public void delete(DOMAIN domain) {
        cache.remove(extractId(domain));
        super.delete(domain);
    }

    @Override
    public void deleteAll() {
        cache.clear();
        super.deleteAll();
    }

    @Override
    public void deleteAll(List<DOMAIN> domains) {
        domains.forEach(domain -> cache.remove(extractId(domain)));
        super.deleteAll(domains);
    }

    @Override
    public void deleteById(ID id) {
        cache.remove(id);
        super.deleteById(id);
    }

    @Override
    public List<DOMAIN> findAll() {
        return super.findAll()
            .stream()
            .peek(domain -> cache.put(extractId(domain), domain))
            .toList();
    }

    @Override
    public List<DOMAIN> findAllById(Iterable<ID> ids) {
        List<DOMAIN> result = new ArrayList<>();
        List<ID> missing = new ArrayList<>();

        ids.forEach(id -> Optional.ofNullable(cache.get(id))
            .ifPresentOrElse(
                result::add,
                () -> missing.add(id)
            ));

        if (!missing.isEmpty()) {
            List<DOMAIN> allById = super.findAllById(missing);
            allById.forEach(domain -> {
                cache.put(extractId(domain), domain);
                result.add(domain);
            });
        }

        return result;
    }

    @Override
    public Optional<DOMAIN> findById(ID id) {
        Optional<DOMAIN> result = Optional.ofNullable(cache.get(id));

        if (result.isEmpty()) {
            result = super.findById(id);
            result.ifPresent(domain -> cache.put(extractId(domain), domain));
        }

        return result;
    }

    @Override
    public void save(DOMAIN domain) {
        if (shouldSave(domain)) {
            cache.put(extractId(domain), domain);
            super.save(domain);
        }
    }

    @Override
    public void saveAll(Collection<DOMAIN> domains) {
        List<DOMAIN> toSave = domains.stream()
            .filter(this::shouldSave)
            .toList();

        toSave.forEach(domain -> cache.put(extractId(domain), domain));
        super.saveAll(toSave);
    }

    protected abstract ID extractId(DOMAIN domain);

    protected boolean shouldSave(DOMAIN domain) {
        return !Objects.equals(cache.get(extractId(domain)), domain);
    }

    public int getCacheSize() {
        return cache.size();
    }
}
