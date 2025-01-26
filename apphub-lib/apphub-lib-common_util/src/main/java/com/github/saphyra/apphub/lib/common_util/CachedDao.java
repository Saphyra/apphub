package com.github.saphyra.apphub.lib.common_util;

import com.github.saphyra.apphub.lib.common_util.cache.AbstractCache;
import com.github.saphyra.apphub.lib.common_util.converter.Converter;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class CachedDao<ENTITY, DOMAIN, ID, REPOSITORY extends CrudRepository<ENTITY, ID>> extends AbstractDao<ENTITY, DOMAIN, ID, REPOSITORY> {
    protected final AbstractCache<ID, DOMAIN> cache;

    protected CachedDao(Converter<ENTITY, DOMAIN> converter, REPOSITORY repository, boolean preLoad, AbstractCache<ID, DOMAIN> cache) {
        super(converter, repository);
        this.cache = cache;
        if (preLoad) {
            findAll();
        }
    }

    @Override
    public void delete(DOMAIN domain) {
        cache.invalidate(extractId(domain));
        super.delete(domain);
    }

    @Override
    public void deleteAll() {
        cache.clear();
        super.deleteAll();
    }

    @Override
    public void deleteAll(List<DOMAIN> domains) {
        domains.forEach(domain -> cache.invalidate(extractId(domain)));

        super.deleteAll(domains);
    }

    @Override
    public void deleteById(ID id) {
        cache.invalidate(id);
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

        ids.forEach(id -> cache.getIfPresent(id)
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
        Optional<DOMAIN> result = cache.getIfPresent(id);

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
    public void saveAll(List<DOMAIN> domains) {
        List<DOMAIN> toSave = domains.stream()
            .filter(this::shouldSave)
            .toList();

        toSave.forEach(domain -> cache.put(extractId(domain), domain));
        super.saveAll(toSave);
    }

    protected abstract ID extractId(DOMAIN domain);

    protected abstract boolean shouldSave(DOMAIN domain);
}
