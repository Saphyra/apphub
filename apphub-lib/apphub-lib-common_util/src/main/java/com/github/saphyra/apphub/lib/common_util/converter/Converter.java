package com.github.saphyra.apphub.lib.common_util.converter;

import java.util.List;
import java.util.Optional;

public interface Converter<E, D> {
    D convertEntity(E entity);

    Optional<D> convertEntity(Optional<E> entity);

    Optional<D> convertEntityToOptional(E entity);

    List<D> convertEntity(List<E> entityList);

    E convertDomain(D domain);

    Optional<E> convertDomain(Optional<D> domain);

    Optional<E> convertDomainToOptional(D domain);

    List<E> convertDomain(List<D> domainList);
}
