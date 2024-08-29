package com.github.saphyra.apphub.integration.core.util;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
public class AutoCloseableImpl<T> implements AutoCloseable {
    @Getter
    private final T object;
    private final Runnable close;

    @Override
    public void close() {
        close.run();
    }
}
