package com.github.saphyra.apphub.lib.encryption_dao;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class EncryptionConverter<E, D> {
    public D convertEntity(E entity) {
        if (entity == null) {
            return null;
        }
        return processEntityConversion(entity);
    }

    public Optional<D> convertEntity(Optional<E> entity) {
        return entity.flatMap(this::convertEntityToOptional);
    }

    public Optional<D> convertEntityToOptional(E entity) {
        if (entity == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(convertEntity(entity));
    }

    public List<D> convertEntity(List<E> entityList) {
        if (entityList == null) {
            throw new IllegalArgumentException("entityList must not be null.");
        }
        return entityList.stream().map(this::convertEntity).collect(Collectors.toList());
    }

    public E convertDomain(D domain) {
        if (domain == null) {
            return null;
        }
        return processDomainConversion(domain);
    }

    public Optional<E> convertDomain(Optional<D> domain) {
        return domain.flatMap(this::convertDomainToOptional);
    }

    public Optional<E> convertDomainToOptional(D domain) {
        if (domain == null) {
            return Optional.empty();
        }
        return Optional.of(convertDomain(domain));
    }

    public List<E> convertDomain(List<D> domainList) {
        if (domainList == null) {
            throw new IllegalArgumentException("domainList must not be null.");
        }
        return domainList.stream().map(this::convertDomain).collect(Collectors.toList());
    }

    protected abstract E processDomainConversion(D domain);

    protected abstract D processEntityConversion(E entity);
}
