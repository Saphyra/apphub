package com.github.saphyra.apphub.lib.common_util;

import com.github.saphyra.apphub.lib.common_util.converter.Converter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public abstract class AbstractDao<ENTITY, DOMAIN, ID, REPOSITORY extends CrudRepository<ENTITY, ID>> {
    protected final Converter<ENTITY, DOMAIN> converter;
    protected final REPOSITORY repository;

    public void delete(DOMAIN domain) {
        repository.delete(converter.convertDomain(domain));
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public void deleteAll(List<DOMAIN> domains) {
        if (domains.isEmpty()) {
            return;
        }

        repository.deleteAll(converter.convertDomain(domains));
    }

    public void deleteById(ID id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            log.debug("No record to delete with id " + id);
        }
    }

    public List<DOMAIN> findAll() {
        Iterable<ENTITY> entities = repository.findAll();
        List<ENTITY> entityList = new ArrayList<>();
        entities.forEach(entityList::add);
        return converter.convertEntity(entityList);
    }

    public List<DOMAIN> findAllById(Iterable<ID> ids) {
        Iterable<ENTITY> entities = repository.findAllById(ids);
        List<ENTITY> entityList = new ArrayList<>();
        entities.forEach(entityList::add);
        return converter.convertEntity(entityList);
    }

    public Optional<DOMAIN> findById(ID id) {
        return converter.convertEntity(repository.findById(id));
    }

    public void save(DOMAIN domain) {
        repository.save(converter.convertDomain(domain));
    }

    public DOMAIN saveAndReturn(DOMAIN domain) {
        return converter.convertEntity(repository.save(converter.convertDomain(domain)));
    }

    public void saveAll(List<DOMAIN> domains) {
        if (domains.isEmpty()) {
            return;
        }

        repository.saveAll(converter.convertDomain(domains));
    }
}
