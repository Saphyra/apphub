package com.github.saphyra.apphub.lib.common_util.collection;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.Optional;

public interface OptionalMap<K, V> extends Map<K, V> {
    default Optional<V> getOptional(K key) {
        return Optional.ofNullable(get(key));
    }

    default V getValidated(K key, String whatNotFound) {
        return getOptional(key)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "%s not found by id %s".formatted(whatNotFound, key)));
    }
}
