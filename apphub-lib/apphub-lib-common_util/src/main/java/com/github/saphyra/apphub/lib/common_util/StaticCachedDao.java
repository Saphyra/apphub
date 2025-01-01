package com.github.saphyra.apphub.lib.common_util;

import com.github.saphyra.apphub.lib.common_util.converter.Converter;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

//TODO unit test
public abstract class StaticCachedDao<ENTITY, DOMAIN, ID, REPOSITORY extends CrudRepository<ENTITY, ID>> extends AbstractDao<ENTITY, DOMAIN, ID, REPOSITORY> {
    protected Map<ID, DOMAIN> cache = new ConcurrentHashMap<>();

    protected StaticCachedDao(Converter<ENTITY, DOMAIN> converter, REPOSITORY repository, boolean preLoad) {
        super(converter, repository);
        if (preLoad) {
            findAll();
        }
    }

    @Override
    public void delete(DOMAIN domain) {
        cache.remove(idExtractor(domain));
        super.delete(domain);
    }

    @Override
    public void deleteAll() {
        cache.clear();
        super.deleteAll();
    }

    @Override
    public void deleteAll(List<DOMAIN> domains) {
        domains.forEach(domain -> cache.remove(idExtractor(domain)));

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
            .peek(domain -> cache.put(idExtractor(domain), domain))
            .toList();
    }

    @Override
    public List<DOMAIN> findAllById(Iterable<ID> ids) {
        List<DOMAIN> result = new ArrayList<>();
        List<ID> missing = new ArrayList<>();

        ids.forEach(id -> findById(id)
            .ifPresentOrElse(
                result::add,
                () -> missing.add(id)
            ));

        if (!missing.isEmpty()) {
            result.addAll(super.findAllById(missing));
        }

        return result;
    }

    @Override
    public Optional<DOMAIN> findById(ID id) {
        Optional<DOMAIN> result = Optional.ofNullable(cache.get(id));

        if (result.isEmpty()) {
            result = super.findById(id);
            result.ifPresent(domain -> cache.put(idExtractor(domain), domain));
        }

        return result;
    }

    @Override
    public void save(DOMAIN domain) {
        if (shouldSave(domain)) {
            cache.put(idExtractor(domain), domain);
            super.save(domain);
        }
    }

    @Override
    public void saveAll(List<DOMAIN> domains) {
        List<DOMAIN> toSave = domains.stream()
            .filter(this::shouldSave)
            .toList();

        toSave.forEach(domain -> cache.put(idExtractor(domain), domain));
        super.saveAll(toSave);
    }

    protected abstract ID idExtractor(DOMAIN domain);

    protected abstract boolean shouldSave(DOMAIN domain);
}
