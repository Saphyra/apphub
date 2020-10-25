package com.github.saphyra.apphub.lib.common_util;

import com.github.saphyra.apphub.lib.common_util.converter.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
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
        repository.deleteAll(converter.convertDomain(domains));
    }

    public void deleteById(ID id) {
        repository.deleteById(id);
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
        return repository.findById(id).map(converter::convertEntity);
    }

    public void save(DOMAIN domain) {
        repository.save(converter.convertDomain(domain));
    }

    public void saveAll(List<DOMAIN> iterable) {
        repository.saveAll(converter.convertDomain(iterable));
    }
}
