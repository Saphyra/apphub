package com.github.saphyra.apphub.lib.common_util.converter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class ConverterBase<E, D> implements Converter<E, D> {
    @Override
    public D convertEntity(E entity) {
        if (entity == null) {
            return null;
        }
        return processEntityConversion(entity);
    }

    protected abstract D processEntityConversion(E entity);

    @Override
    public Optional<D> convertEntity(Optional<E> entity) {
        return entity.flatMap(this::convertEntityToOptional);
    }

    @Override
    public Optional<D> convertEntityToOptional(E entity) {
        if (entity == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(convertEntity(entity));
    }

    @Override
    public List<D> convertEntity(List<E> entityList) {
        if (entityList == null) {
            throw new IllegalArgumentException("entityList must not be null.");
        }
        return entityList.stream().map(this::convertEntity).collect(Collectors.toList());
    }

    @Override
    public E convertDomain(D domain) {
        if (domain == null) {
            return null;
        }
        return processDomainConversion(domain);
    }

    protected abstract E processDomainConversion(D domain);

    @Override
    public Optional<E> convertDomain(Optional<D> domain) {
        return domain.flatMap(this::convertDomainToOptional);
    }

    @Override
    public Optional<E> convertDomainToOptional(D domain) {
        if (domain == null) {
            return Optional.empty();
        }
        return Optional.of(convertDomain(domain));
    }

    @Override
    public List<E> convertDomain(List<D> domainList) {
        if (domainList == null) {
            throw new IllegalArgumentException("domainList must not be null.");
        }
        return domainList.stream().map(this::convertDomain).collect(Collectors.toList());
    }
}
